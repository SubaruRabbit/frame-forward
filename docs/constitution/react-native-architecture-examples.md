# React Native 架构规范：实践示例附录

配套[架构规范 v1.6.0](./react-native-project-architecture-specification.md)，修订日期：2026-09-08。

本附录用同一套订单场景说明职责、调用路径与验证方法。`features/order` 负责订单页面，`features/checkout` 负责结算流程，`domains/order` 提供被二者消费的订单能力，`domains/session` 维护会话，services 负责外部适配。示例不要求项目创建这些模块。

**阅读与运行范围**

- TypeScript 片段以 TS 5.x 语法为基线；库示例面向 TanStack Query 5.x、Zustand 5.x、React Navigation 7.x。RN、React、Jest 及工程插件必须采用项目相互兼容且锁定的版本；本仓库只有规范文档，没有可启动的 RN 工程。
- 标记为“片段”的代码需放入指定文件、补齐说明中的依赖后集成；不同文件之间不要直接拼接。标记为“伪代码”的内容展示算法或装配顺序，不是某个库可直接执行的 API。
- 路径均为**目标工程内的示例路径**，不是本仓库已经创建的源码。只给出被引用能力的公开签名时，不代表其传输、SDK 或界面实现已提供。
- A.1—A.3 的核心逻辑可使用 fake 在无 RN 的环境验证；A.7 是局部规则配置示例，不能代替正文要求的完整检查器；其他片段的集成测试必须在项目实际版本下执行。
- 示例保留关键边界与错误分支，省略完整 UI、真实服务端、签名构建和跨进程锁实现。这里的超时和次数仅用于说明，不是全项目默认参数。

| 示例 | 主要对应条款 | 内容 |
| --- | --- | --- |
| [A.1](#a1) | §3.4、§5.1 | 用例接口、app 注入与 fake 测试 |
| [A.2](#a2) | §4.5、§4.7 | 订单请求、缓存与独立于页面的收尾 |
| [A.3](#a3) | §4.8 | 会话范围、失效与旧请求处理 |
| [A.4](#a4) | §4.5 | 并发刷新、失败释放与重试预算 |
| [A.5](#a5) | §4.7 | 并发更新反例与保守实现 |
| [A.6](#a6) | §5.4 | 初始化合并、清理与后台入口 |
| [A.7](#a7) | §3.1、§7.4 | 例外记录、配置与导入验收 |
| [A.8](#a8) | §3.3、§4.1、§4.6、§5.3 | 公开入口、Provider、翻译、导航及删除 |
| [A.9](#a9) | §4.7 | 网络、应用焦点与页面焦点 |
| [A.10](#a10) | §4.7、§4.8 | Zustand 草稿持久化与恢复门禁 |
| [A.11](#a11) | §2.3、§7.4、§8 | 平台实现、类型检查与 Jest 解析 |

<a id="a1"></a>

## A.1 用例接口、app 注入与 fake 测试

**形式：TypeScript 片段。** 会话类型来自 A.3；订单公开能力来自 A.2。支付模块在此仅约定公开能力签名，省略支付 SDK、服务端结果核验和支付页面。示例中“明确失败保留待支付订单、未知结果返回核验中”是用于说明的产品规则。

消费方声明自己需要的能力，不导入提供方 feature 的类型。

```ts
// src/features/checkout/useCases/createCheckout.ts
import type { SessionScope } from '@contracts/session';

export type CheckoutPorts = {
  assertCurrent(scope: SessionScope): void;
  createOrder(draftId: string, scope: SessionScope): Promise<{ id: string }>;
  pay(orderId: string, scope: SessionScope): Promise<'paid' | 'declined' | 'unknown'>;
};

export function createCheckout(ports: CheckoutPorts) {
  return async function submit(draftId: string, scope: SessionScope) {
    ports.assertCurrent(scope);
    const order = await ports.createOrder(draftId, scope);
    ports.assertCurrent(scope);
    const payment = await ports.pay(order.id, scope);
    ports.assertCurrent(scope);
    if (payment === 'paid') return { orderId: order.id, status: 'complete' as const };
    if (payment === 'declined') return { orderId: order.id, status: 'awaiting-payment' as const };
    return { orderId: order.id, status: 'verifying-payment' as const };
  };
}
```

```ts
// src/features/checkout/index.ts
export { createCheckout } from './useCases/createCheckout';
export type { CheckoutPorts } from './useCases/createCheckout';
```

```ts
// src/app/bootstrap/checkout.ts
import { createCheckout } from '@features/checkout';
import type { OrderCommands } from '@domains/order';
import type { SessionGuard } from '@contracts/session';
import type { PaymentCommands } from '@domains/payment';

// PaymentCommands 的公开签名：pay(id, scope) 返回 paid / declined / unknown。
export function wireCheckout(
  orders: OrderCommands, payments: PaymentCommands, session: SessionGuard,
) {
  return createCheckout({
    assertCurrent: session.assertCurrent,
    createOrder: (draftId, scope) => orders.create(draftId, scope),
    pay: (orderId, scope) => payments.pay(orderId, scope),
  });
}
```

app 只连接能力。反例是在 `pay` 的连接回调里决定“失败后取消订单”；这会把业务决策移入 app。

```ts
// src/features/checkout/useCases/createCheckout.test.ts
import { expect, jest, test } from '@jest/globals';
import { createCheckout } from './createCheckout';
import type { CheckoutPorts } from './createCheckout';

test('创建订单失败时不执行支付', async () => {
  const pay = jest.fn<CheckoutPorts['pay']>();
  const submit = createCheckout({
    assertCurrent: () => {},
    createOrder: async () => { throw new Error('create-failed'); },
    pay,
  });
  await expect(submit('draft-1', {
    accountId: 'A', tenantId: 'T', generation: 'g1',
  })).rejects.toThrow('create-failed');
  expect(pay).not.toHaveBeenCalled();
});
```

还应验证 paid、declined、unknown 三种结果和两次等待之间的会话切换。`unknown` 由后续核验用例继续处理，不能直接自动再扣款；支付异常只有提供方能确认含义时才可转换成明确失败。

<a id="a2"></a>

## A.2 订单请求、缓存与独立于页面的收尾

**形式：TypeScript 集成片段，TanStack Query 5.x。** 展示列表查询和创建订单；不做乐观插入，避免假设订单符合所有列表筛选条件。创建请求默认不自动重试。真实 HTTP 传输、超时、响应校验和鉴权模式由 services 实现，A.3—A.4 说明其会话与重试边界。

业务 API 只消费 `@services/api` 暴露的最小客户端契约。内部 `client.ts` 不对业务开放，泛型也不能替代运行时响应校验；此处只提供类型，真实客户端还须实现正文要求的传输、超时、取消及错误归一化。

```ts
// src/services/api/types.ts
import type { SessionScope } from '@contracts/session';
export type HttpClient = {
  request<T>(input: {
    method: 'GET' | 'POST';
    path: string;
    scope: SessionScope;
    signal?: AbortSignal;
    body?: unknown;
  }): Promise<T>;
};

// src/services/api/index.ts（公开类型片段）
export type { HttpClient } from './types';
```

```ts
// src/domains/order/api/orderApi.ts
import type { HttpClient } from '@services/api';
import type { SessionScope } from '@contracts/session';

export type Order = { id: string; status: string };
type OrderDto = { order_id: string; status: string };
const mapOrder = (dto: OrderDto): Order => ({ id: dto.order_id, status: dto.status });

export function createOrderApi(http: HttpClient) {
  return {
    async list(scope: SessionScope, status: string, signal?: AbortSignal) {
      const rows = await http.request<OrderDto[]>({
        method: 'GET', path: `/orders?status=${encodeURIComponent(status)}`,
        scope, signal,
      });
      return rows.map(mapOrder);
    },
    async create(draftId: string, scope: SessionScope) {
      const dto = await http.request<OrderDto>({
        method: 'POST', path: '/orders', body: { draft_id: draftId }, scope,
      });
      return mapOrder(dto);
    },
  };
}
```

```ts
// src/domains/order/queries/orderKeys.ts
import type { SessionScope } from '@contracts/session';
export const orderKeys = {
  lists: (s: SessionScope) => ['order', s.tenantId, s.accountId, 'list'] as const,
  list: (s: SessionScope, status: string) => [...orderKeys.lists(s), { status }] as const,
};
```

这里账号和租户用于缓存分区，generation 用于会话校验；同账号重新登录时仍需按正文清理或明确恢复缓存，不能只依赖 key。

```ts
// src/domains/order/useCases/createOrderCommands.ts
import type { QueryClient } from '@tanstack/react-query';
import type { SessionGuard, SessionScope } from '@contracts/session';
import type { createOrderApi } from '../api/orderApi';
import { orderKeys } from '../queries/orderKeys';

export function createOrderCommands(
  api: ReturnType<typeof createOrderApi>, cache: QueryClient, session: SessionGuard,
) {
  return {
    async create(draftId: string, scope: SessionScope) {
      session.assertCurrent(scope);
      const order = await api.create(draftId, scope);
      session.assertCurrent(scope);
      // 收尾属于命令：页面卸载或后台调用都经过这里。
      await cache.invalidateQueries({ queryKey: orderKeys.lists(scope) });
      session.assertCurrent(scope);
      return order;
    },
  };
}
export type OrderCommands = ReturnType<typeof createOrderCommands>;
```

创建已成功但随后会话失效时，调用方可能收到失效错误；不能把该错误解释为“服务端未创建”，也不能因此自动重发 POST。示例不等待“所有列表已成功刷新”才承认写入，失效后的读取失败由查询状态表达。

```ts
// src/domains/order/queries/useOrders.ts
import { useQuery } from '@tanstack/react-query';
import type { SessionScope } from '@contracts/session';
import { useOrderRuntime } from '../context';
import { orderKeys } from './orderKeys';

export function useOrders(scope: SessionScope, status: string, ready: boolean) {
  const { api, session } = useOrderRuntime();
  return useQuery({
    queryKey: orderKeys.list(scope, status),
    enabled: ready,
    retry: false, // 示例不引入第二层重试；项目按正文配置预算。
    queryFn: async ({ signal }) => {
      session.assertCurrent(scope);
      const rows = await api.list(scope, status, signal);
      session.assertCurrent(scope);
      return rows;
    },
  });
}
```

模块 hooks 通过内部 Context 取得 api、session、commands（实现见 A.8），页面只消费公开 hooks。认证未就绪时不得展示旧账号结果；`enabled` 只约束自动查询，命令自身仍需校验。

```ts
// src/domains/order/queries/useCreateOrder.ts
import { useMutation } from '@tanstack/react-query';
import type { SessionScope } from '@contracts/session';
import { useOrderRuntime } from '../context';

export function useCreateOrder() {
  const { commands } = useOrderRuntime();
  return useMutation({
    retry: false,
    mutationFn: (input: { draftId: string; scope: SessionScope }) =>
      commands.create(input.draftId, input.scope),
  });
}
```

```ts
// src/domains/order/index.ts（公开入口片段）
export type { OrderCommands } from './useCases/createOrderCommands';
export { useCreateOrder } from './queries/useCreateOrder';
export { useOrders } from './queries/useOrders';
// api、key 工厂与 createOrderCommands 工厂不直接对外开放。
```

页面调用 `mutation.mutate(input, { onSuccess: closeDialog })` 时，`closeDialog` 仅处理仍有效页面的交互。反例是只在这个回调里失效订单列表。后台通过 app 注入的 `commands.create` 调用同一命令，不重复增加第二份失效处理。[回调机制](https://tanstack.com/query/latest/docs/framework/react/guides/mutations)

验证：暂停 fake API 的 Promise，触发命令后卸载调用页面，再完成 Promise；断言相关列表被失效一次。另用 fake QueryClient 验证后台调用有相同收尾，以及会话失效后不再调用缓存写入或失效能力。

<a id="a3"></a>

## A.3 会话范围、失效与旧请求处理

**形式：可独立验证的 TypeScript 核心片段。** 单 JS 运行时模型，省略凭证存储、UI 订阅及多运行时协调。generation 由 session 的会话建立逻辑生成唯一值；调用方不能自行用账号 ID 代替 generation。

```ts
// src/contracts/session.ts
export type SessionScope = Readonly<{
  accountId: string;
  tenantId: string;
  generation: string;
}>;
export type SessionGuard = { assertCurrent(scope: SessionScope): void };
```

```ts
// src/domains/session/state/createSessionGuard.ts
import type { SessionScope } from '@contracts/session';

export function createSessionGuard() {
  let current: SessionScope | undefined;
  return {
    // 仅供 session 内部会话建立流程使用，完成隔离后才 publish。
    publish(scope: SessionScope) { current = Object.freeze({ ...scope }); },
    invalidate() { current = undefined; },
    capture(): SessionScope {
      if (!current) throw new Error('session-not-ready');
      return current;
    },
    assertCurrent(scope: SessionScope) {
      if (!current || current.accountId !== scope.accountId ||
          current.tenantId !== scope.tenantId || current.generation !== scope.generation) {
        throw new Error('session-expired');
      }
    },
  };
}
```

app 从 session 公开能力取得 capture／assertCurrent，普通业务不获得 publish／invalidate。HTTP 传输在异步取得凭证后还要再次校验；业务结果提交前也要校验。

```text
目标文件：services/api/client.ts 内部发送路径（伪代码）
scope = 请求最初捕获的范围
assertCurrent(scope)
credential = await sessionCredentialPort.get(scope)
assertCurrent(scope)
检查 signal、预算，再把 credential 交给传输
response = await transport(...)
assertCurrent(scope)
返回 response；调用方每次跨过新的异步等待后，提交副作用前再次校验
```

| 时刻 | 动作 | 结果 |
| --- | --- | --- |
| t0 | A 登录，发布 g1；请求 R 捕获 A/T/g1 | R 属于 g1 |
| t1 | 退出，使 g1 失效，停用请求并清理或隔离状态 | R 即使不能取消也失去提交权限 |
| t2 | A 再次登录，完成恢复后发布 g2 | 账号相同，generation 不同 |
| t3 | R 的响应、失败回调或持久化恢复返回 | 校验失败，不更新缓存、不清除 g2、不跳转页面 |

验证还应包含 A→B，以及异步取得凭证期间切换账号。会话失效不能取消服务端已经执行的写入；结果不确定时由后续业务核验处理。

<a id="a4"></a>

## A.4 并发刷新、失败释放与重试预算

**形式：时间线与伪代码，不是可复制的 HTTP client。** 目标文件为 `domains/session/refresh/refreshCredential.ts`、`services/api/client.ts`，app 通过接口连接双方；具体服务端错误码、凭证版本、原生取消与跨运行时锁由项目补齐。

先选择一个容易核验的示例策略：普通 Query／client 网络重试均关闭；每个业务逻辑请求最多发送两次（初次与一次鉴权重放）；同组凭证的一波并发刷新最多一次发送、不自动重试。业务请求各自有截止时间，刷新也有独立超时。若两个请求均需重放，这一波最多两次初始请求、一次刷新、两次重放，共五次传输；不是每个请求各刷新一次。

```text
请求 R1、R2 都使用凭证版本 c1，捕获相同会话 g1
R1 认证失败 → session 建立刷新 Promise F(g1, c1)
R2 认证失败 → 等待同一个 F；不再发送刷新请求
F 成功 → 校验 g1 仍有效 → 原子发布凭证 c2 → 结束所有有效等待者
R1、R2 各自检查取消、会话与剩余预算 → 最多重放一次
旧 c1 响应晚到 → 发现已发布 c2 → 按自己的重放预算使用 c2，不再刷新
F 失败或超时 → 结束所有等待者，移除进行中记录，错误上抛
F 完成前切到 g2 → 不发布旧刷新结果，也不将等待者改绑到 g2
```

```text
session.refresh(scope, failedCredentialVersion):
  assertCurrent(scope)
  如果当前凭证版本已更新：返回当前版本
  如果同 scope 和失败版本已有进行中刷新：等待它
  否则在第一次 await 前登记共享 Promise：
    try:
      经 services.refreshTransport 发送刷新（禁用自动刷新拦截、设置超时）
      assertCurrent(scope)
      仅当失败版本仍为当前版本时提交新凭证
      返回有效凭证
    finally:
      仅清除仍指向本 Promise 的记录

client.execute(operation):
  operation 在最外层调用时创建；上层重试复用同一个对象
  operation 包含 scope、remainingSends=2、authReplayed=false、deadline、signal
  每次实际发送前检查 scope、signal、deadline，并扣减 remainingSends
  初次发送收到“可刷新”的认证错误时：
    若 authReplayed 或写入不满足重放条件：失败退出
    先设置 authReplayed=true，再等待 session.refresh
    等待后重新检查 scope、signal、deadline，再发送一次
  重放仍失败：向调用方返回错误，不重新启动刷新
```

共享刷新不能使用任意一个等待者的取消信号作为唯一生命周期：R1 取消应停止 R1 等待／重放，不能无意中取消 R2 仍依赖的刷新。刷新自身超时或会话失效则应终止全部相关等待者。

**反例：** 刷新函数再次调用默认鉴权 client，默认 client 又等待刷新函数；或者每次 Query 重试都新建预算对象。这两种写法分别导致自等待与预算失效。验证时统计底层传输次数，并用可控 Promise 分别触发成功、超时、切换会话和单个等待者取消。

<a id="a5"></a>

## A.5 并发覆盖反例与保守实现

**形式：时间线与可独立验证的 TypeScript 核心片段。** 目标为订单备注修改；没有服务端版本支持时，示例选择同实体串行且不做乐观写入，不展示不完整的通用回滚框架。

| 时刻 | 无保护的乐观更新 | 问题 |
| --- | --- | --- |
| t0 | 原备注为 v0，A 保存 v0 快照后写入 v1 | A 尚未完成 |
| t1 | B 写入 v2 并成功 | v2 已成为有效更新 |
| t2 | A 失败，直接恢复 v0 | B 的结果被旧快照覆盖 |

```ts
// src/domains/order/useCases/createSerialExecutor.ts
export function createSerialExecutor() {
  const tails = new Map<string, Promise<void>>();
  return function run<T>(key: string, work: () => Promise<T>): Promise<T> {
    const previous = tails.get(key) ?? Promise.resolve();
    const result = previous.then(work);
    const tail = result.then(() => {}, () => {});
    tails.set(key, tail);
    return result.finally(() => {
      if (tails.get(key) === tail) tails.delete(key);
    });
  };
}
```

```text
目标文件：domains/order/useCases/updateOrderNote.ts（集成伪代码）
模块实例只创建一个 run = createSerialExecutor()
队列键 = JSON.stringify([tenantId, accountId, generation, orderId])
run(队列键, async () => {
  检查会话、取消与剩余预算；排队期间可能已经退出登录
  发送备注修改；不预先覆盖 Query 缓存，也不保存待回滚的旧快照
  检查会话
  对相关查询失效并按策略校准，错误时也考虑服务端结果是否未知
  返回写入结果；同实体的下一个操作此后才开始
})
```

这里串行的是整个本地命令，包括收尾，不只是底层网络请求。不同实体可并行；列表缓存被多个实体共同影响时，须协调列表校准或统一在相关写入结束后校准。这个内存队列不解决其他设备或进程的并发写入；需要更强保证时须采用服务端版本或冲突协议。

验证：保持 A 的 Promise 未完成，确认 B 尚未调用传输；让 A 失败后确认 B 仍会执行；确认整个过程中没有“恢复 v0”的写入。若改用乐观更新，则必须另测 A 失败／旧成功结果不覆盖 B 的路径。

<a id="a6"></a>

## A.6 初始化合并、失败回收与后台入口

**形式：TypeScript 核心片段与集成伪代码。** 单 JS 运行时内通过 Promise 复用初始化；具体 SDK 初始化、凭证恢复及依赖构造放在注入的 start 中。初始化成功不表示此后会话永远有效，任务执行时仍用 A.3 检查。

```ts
// src/app/bootstrap/createRuntimeInitializer.ts
type Dispose = () => Promise<void>;
type RegisterCleanup = (dispose: Dispose) => void;

export function createRuntimeInitializer<T>(
  start: (registerCleanup: RegisterCleanup) => Promise<T>,
) {
  let pending: Promise<T> | undefined;
  let blocked: Error | undefined;
  return function ensureReady(): Promise<T> {
    if (blocked) return Promise.reject(blocked);
    if (pending) return pending;
    const cleanups: Dispose[] = [];
    pending = Promise.resolve().then(async () => {
      try {
        return await start((dispose) => { cleanups.push(dispose); });
      } catch (error) {
        const failures: unknown[] = [];
        for (const dispose of cleanups.reverse()) {
          try { await dispose(); } catch (cleanupError) { failures.push(cleanupError); }
        }
        if (failures.length) {
          blocked = new Error('runtime-cleanup-failed', { cause: { error, failures } });
        }
        pending = undefined;
        throw blocked ?? error;
      }
    });
    return pending;
  };
}
```

构造器仅由 app 创建一次。成功后 Promise 保留并复用；失败且回收成功则允许下一次调用重试；回收失败进入显式失败状态，不能自动重新注册一遍监听。恢复方式由 app 决定，例如重新建立运行时。

```text
目标文件：app/bootstrap/runtime.ts（装配伪代码）
ensureRuntimeReady = createRuntimeInitializer(async registerCleanup => {
  每获得一个资源，就立即登记可等待的 dispose
  读取并校验配置
  创建 services，注入 session 的鉴权能力
  恢复本次任务所需会话／状态；先满足基础传输，再恢复依赖传输的会话
  创建订单命令、可选 QueryClient 等依赖
  返回 runtime：含命令、会话接口及运行时共享资源的 dispose
})

目标文件：app/bootstrap/orderTask.ts（装配伪代码）
async runOrderTask(payload) {
  runtime = await ensureRuntimeReady()
  校验 payload 的账号／租户并捕获有效会话范围
  try: await runtime.orders 执行任务（继续检查 scope 与预算）
  finally: 释放本任务私有资源；不调用 runtime.dispose()
}

目标文件：index.js（早期注册伪代码）
立即将 app 的 runOrderTask 注册到平台任务入口
任务回调自身返回上述 Promise，不等待 AppProviders 挂载

目标文件：App.tsx（前台伪代码）
等待同一个 ensureRuntimeReady()；显示就绪或失败状态
就绪后把 runtime 交给 Providers，不在每次渲染重建依赖
```

成功运行时的 dispose 由 app 在不再有前台或后台消费者时调用；测试应为每次用例新建 initializer。上述片段只实现失败回收，成功资源的引用管理和进程退出行为不包含在内。

验证：同时调用 ensureReady 两次，start 只执行一次；第二步初始化失败，第一步资源只清理一次；清理成功后下一次调用可重试；清理失败时后续调用继续失败；后台任务结束不销毁前台共享客户端。

<a id="a7"></a>

## A.7 例外记录、配置与导入验收

**形式：JSON 记录、局部 dependency-cruiser 配置和验收文件。** 不绑定该工具；使用它时须安装并锁定支持示例选项的版本。以下配置只演示两条规则，**不代表已经覆盖正文完整矩阵、公开入口、SDK 和测试权限**。

```json
{
  "from": "domains/cart",
  "to": "domains/product",
  "entry": "@domains/product",
  "exports": ["ProductId"],
  "importKind": "type",
  "reason": "购物车条目引用商品标识类型，类型仍由商品模块维护",
  "owner": "cart 模块负责人",
  "review": "示例：实际采用时替换为真实评审记录"
}
```

目标文件：`architecture/domain-dependencies.json` 中的一条记录。它是项目约定的数据格式，不是 dependency-cruiser 原生配置；入口解析和具体导出项须由项目规则生成器或符号检查规则读取。

```js
// architecture/dependency-cruiser.example.cjs
module.exports = {
  forbidden: [
    {
      name: 'utils-must-not-import-services', severity: 'error',
      from: { path: '^src/utils/' },
      to: { path: '^src/services/' },
    },
    {
      name: 'no-runtime-cycles', severity: 'error', from: {},
      to: { circular: true, viaOnly: { dependencyTypesNot: ['type-only'] } },
    },
  ],
  options: {
    tsConfig: { fileName: 'tsconfig.json' },
    tsPreCompilationDeps: true,
    doNotFollow: { path: 'node_modules' },
  },
};
```

`viaOnly` 区分整条环是否仅包含运行时边；仅在环的一条边上排除类型导入并不足以判断整条环。选项依据 [dependency-cruiser 规则说明](https://github.com/sverweij/dependency-cruiser/blob/main/doc/rules-reference.md)。此处使用通用解析演示；平台配置须按 A.11 调整。

在目标工程创建上述配置，且已安装锁定的 dependency-cruiser 和 TypeScript 后运行：

```sh
npm exec -- depcruise --config architecture/dependency-cruiser.example.cjs --output-type err src
```

以下是**独立验收 fixture**，不是应该加入生产代码的文件；每个拒绝案例应在单独 fixture 工程或隔离变体中运行，不能让 CI 永久扫描一组故意违规的源码。

```ts
// fixture allow：src/services/api/check.ts
import { formatDate } from '@utils/date';
export const formatted = formatDate(new Date(0));
```

```ts
// fixture deny：src/utils/bad.ts
import { http } from '@services/api';
export const leaked = http;
```

fixture 需提供最小真实入口：`utils/date/index.ts` 导出 `formatDate`、`services/api/index.ts` 导出 fake `http`。预期 allow 退出码为 0；deny 非 0 且错误包含 `utils-must-not-import-services`。不能只断言“工具运行过”。

完整项目检查器对上面的 cart 例外还需执行如下逻辑：

```text
目标文件：architecture/check-entry-exports.ts（项目实现伪代码）
遍历 import / export / require 的已解析依赖
先按正文矩阵校验来源区域与目标区域；未识别的生产目录拒绝
domain 跨模块导入：匹配来源、目标、公开入口及导入类型的已批准记录
对命名导入逐个检查真实导出项，跟踪重命名和转导出
ProductId 仅类型导入允许；其他导出项、运行时导入、内部深路径拒绝
命名空间或 export * 无法证明仅暴露批准项时拒绝，不能视为自动获准
再检查模块级领域依赖环（包括类型）和各平台运行时环
```

补充验收包括 `import type { ProductId as Id }` 允许、`import { loadProduct }` 拒绝、深路径与反向依赖拒绝。SDK 受限调用由 ESLint 语法／符号规则补足；配置中应明确扫描的测试辅助路径和 root 装配例外，不能声称一个依赖图规则已覆盖所有语义。

<a id="a8"></a>

## A.8 公开入口、Provider、翻译、导航及删除

**形式：React／TypeScript 片段及装配伪代码。** 衔接 A.2，不重复定义第二个订单模块。Provider 用 `createElement` 表达，等价于 JSX；完整页面样式、导航器和翻译引擎实现省略。

```ts
// src/domains/order/context.ts
import { createContext, useContext } from 'react';
import type { SessionGuard } from '@contracts/session';
import type { createOrderApi } from './api/orderApi';
import type { OrderCommands } from './useCases/createOrderCommands';

export type OrderRuntime = {
  api: ReturnType<typeof createOrderApi>;
  commands: OrderCommands;
  session: SessionGuard;
};
export const OrderContext = createContext<OrderRuntime | null>(null);
export function useOrderRuntime() {
  const value = useContext(OrderContext);
  if (!value) throw new Error('order-provider-missing');
  return value;
}
```

```ts
// src/domains/order/registration.ts：生产代码仅由 app 导入
import { createElement } from 'react';
import type { ReactNode } from 'react';
import type { QueryClient } from '@tanstack/react-query';
import type { HttpClient } from '@services/api';
import type { SessionGuard } from '@contracts/session';
import { createOrderApi } from './api/orderApi';
import { createOrderCommands } from './useCases/createOrderCommands';
import { OrderContext } from './context';

export function createOrderRegistration(
  http: HttpClient, cache: QueryClient, session: SessionGuard,
) {
  const api = createOrderApi(http);
  const commands = createOrderCommands(api, cache, session);
  const value = { api, commands, session };
  function Provider({ children }: { children?: ReactNode }) {
    return createElement(OrderContext.Provider, { value }, children);
  }
  return { Provider, commands };
}
```

app 在运行时初始化中调用一次工厂，把 `commands` 交给 A.1 的 `wireCheckout`，把稳定的 `Provider` 交给 `AppProviders`；不能在每次根组件渲染时重新调用工厂。领域 `index.ts` 保持 A.2 的公开 hooks／类型，不转导出内部 Context 或装配工厂。

```ts
// src/features/order/index.ts：页面是本 feature 的公开能力
export { OrderListScreen } from './screens/OrderListScreen';

// src/features/order/registration.ts：文件片段，业务页面不导入它
import zhCN from './locales/zh-CN.json';
import en from './locales/en.json';
export const orderResources = { 'zh-CN': { order: zhCN }, en: { order: en } };
```

```json
{
  "title": "订单",
  "create": "创建订单"
}
```

上面的 JSON 位于 `features/order/locales/zh-CN.json`，英文文件包含同名键。页面使用 `order` 命名空间，不写死用户文案。

```ts
// src/contracts/navigation.ts
export type RootStackParamList = {
  OrderList: undefined;
  OrderDetail: { orderId: string };
};
```

```text
目标文件：app/providers/AppProviders.tsx 与 app/navigation/RootNavigator.tsx（伪代码）
从 domains/order/registration 导入 createOrderRegistration
从 features/order/registration 导入 orderResources
从 features/order 公开入口导入页面，从 contracts/navigation 取得参数类型
app 把 orderResources 交给 @i18n 的资源注册接口
QueryClientProvider → 订单 Provider → Navigator
Navigator 按 RootStackParamList 注册页面
页面调用 navigate('OrderDetail', { orderId })，不反向导入 RootNavigator
i18n 引擎只接收传入资源，不扫描或导入 features
```

| 删除 `features/order` 时 | 对应动作 |
| --- | --- |
| app 导航注册及外部跳转 | 移除页面注册、调用方跳转和不再使用的路由类型／深链接 |
| app 翻译资源注册 | 移除 orderResources 的导入和注册 |
| feature 私有状态及测试 | 清理其重置、持久化策略和相关测试 |
| `domains/order` | checkout 仍使用时保留；不能随订单页面一起删除 |
| domain Provider | 只为已删除页面服务时可移除挂载；非 React 命令是否保留按剩余调用方判断 |

验证 app 装配只经指定入口；根集成测试通过 app 测试装配入口替换传输，不直接 mock `domains/order/api/orderApi.ts`。

<a id="a9"></a>

## A.9 网络、应用焦点与页面焦点

**形式：TypeScript 片段和页面策略伪代码，TanStack Query 5.x。** 示例选择未知网络状态暂时暂停请求；项目可以另定策略，但不能把未知当作已确认联网。NetInfo 必须采用与项目 RN 兼容的锁定版本。

```ts
// src/services/network/subscribeNetwork.ts
import NetInfo from '@react-native-community/netinfo';

export function subscribeNetwork(listener: (online: boolean | null) => void) {
  return NetInfo.addEventListener((state) => {
    if (state.isConnected === false || state.isInternetReachable === false) listener(false);
    else if (state.isConnected === true && state.isInternetReachable === true) listener(true);
    else listener(null);
  });
}
// src/services/network/index.ts 从本文件导出 subscribeNetwork。
```

```ts
// src/app/query/lifecycle.ts
import { AppState } from 'react-native';
import { focusManager, onlineManager } from '@tanstack/react-query';
import { subscribeNetwork } from '@services/network';

// 只由 app 的运行时初始化调用一次；本函数不负责多调用者引用计数。
export function attachQueryLifecycle() {
  onlineManager.setOnline(false); // 首次网络事件到来前使用明确的未知策略。
  onlineManager.setEventListener((setOnline) =>
    subscribeNetwork((online) => setOnline(online === true)),
  );
  focusManager.setFocused(AppState.currentState === 'active');
  const subscription = AppState.addEventListener('change', (state) => {
    focusManager.setFocused(state === 'active');
  });
  return () => {
    subscription.remove();
    // 替换监听工厂会清理之前由它注册的网络监听。
    onlineManager.setEventListener(() => () => {});
  };
}
```

全局管理器接入依据 [TanStack Query RN 指南](https://tanstack.com/query/latest/docs/framework/react/react-native)。该清理函数只属于 app 运行时；页面不能调用它，其他模块也不能并行重设这两个全局管理器的监听。

```text
目标文件：features/order/hooks/useOrdersScreenFocus.ts（页面策略伪代码）
通过公开 useOrders 取得本页面查询结果
useFocusEffect 首次聚焦不额外刷新（初次挂载已由 query 发起）
后续聚焦时：只有会话就绪、该查询过期、没有进行中的读取，才 refetch 本查询
用稳定回调读取最新查询状态，避免 isStale 改变触发重复聚焦副作用
不调用无范围的 refetchQueries()，不在页面注册 AppState 或 NetInfo 全局监听
```

验证监听只注册一次、dispose 后不再通知；模拟后台→前台与网络恢复；页面首次挂载不重复请求，重新聚焦只刷新订单相关查询。

<a id="a10"></a>

## A.10 Zustand 草稿持久化与恢复门禁

**形式：Zustand 5.x TypeScript 片段。** 采用 vanilla store 和显式保存／恢复，便于展示异步门禁；不要求使用 persist 中间件。`KeyValueStorage` 由 `@services/storage` 提供，其 getItem／setItem／removeItem 均为 Promise 接口，底层 SDK 省略。

示例保存订单备注草稿，持久化字段只有 note 和 schema version；不保存 Query 数据、ready、凭证或 actions。每个会话创建自己的模型实例，app 切换时先停止并排空旧实例写入，再允许新实例恢复相同账号分区。

```ts
// src/features/order/state/createOrderDraft.ts
import { createStore } from 'zustand/vanilla';
import type { SessionGuard, SessionScope } from '@contracts/session';
import type { KeyValueStorage } from '@services/storage';

type DraftState = { note: string; ready: boolean };

function decodeDraft(raw: string | null): string {
  if (raw === null) return '';
  const data: unknown = JSON.parse(raw);
  if (!data || typeof data !== 'object') throw new Error('invalid-draft');
  const record = data as Record<string, unknown>;
  if (record.version === 2 && typeof record.note === 'string') return record.note;
  if (record.version === 1 && typeof record.noteText === 'string') return record.noteText;
  throw new Error('unsupported-draft-version');
}

export function createOrderDraft(
  scope: SessionScope, session: SessionGuard, storage: KeyValueStorage,
) {
  const key = JSON.stringify(['order-draft', scope.tenantId, scope.accountId]);
  const store = createStore<DraftState>()(() => ({ note: '', ready: false }));
  let stopped = false;
  let writes: Promise<void> = Promise.resolve();
  let hydration: Promise<void> | undefined;
  function assertActive() {
    if (stopped) throw new Error('draft-disposed');
    session.assertCurrent(scope);
  }
  return {
    store, // 模块内部 Provider 使用，不从 feature 的 index.ts 导出裸 store。
    hydrate() {
      if (hydration) return hydration;
      hydration = Promise.resolve().then(async () => {
        assertActive();
        const note = decodeDraft(await storage.getItem(key));
        assertActive();
        store.setState({ note, ready: true });
      }).catch((error) => { hydration = undefined; throw error; });
      return hydration;
    },
    setNote(note: string) {
      assertActive();
      if (!store.getState().ready) throw new Error('draft-not-ready');
      store.setState({ note });
    },
    save() {
      assertActive();
      if (!store.getState().ready) throw new Error('draft-not-ready');
      const raw = JSON.stringify({ version: 2, note: store.getState().note });
      const result = writes.then(async () => {
        assertActive();
        await storage.setItem(key, raw);
        assertActive(); // 物理写入可能已完成，但旧会话不再接收“保存成功”。
      });
      writes = result.then(() => {}, () => {});
      return result; // 调用方必须处理失败，不能显示“已保存”。
    },
    async dispose() {
      stopped = true;
      await writes; // 等待已开始的写入；尚未开始的写入会被 assertActive 拒绝。
    },
    async clearAfterDispose() {
      if (!stopped) throw new Error('dispose-required');
      await writes;
      await storage.removeItem(key);
    },
  };
}
```

读取失败或数据版本不支持时 hydrate 拒绝，ready 保持 false；由页面／启动流程显示恢复失败并提供重试或经产品定义的清除重建入口。版本 1 的 noteText 读取后转换为当前内存字段，下次保存写入版本 2。

模块内部通过 `useStore(model.store, state => state.ready)` 和 note 的 selector 订阅；页面消费模块公开 hook 和 setNote／save 操作，不获得任意 setState 权限。app 调用 dispose 后，按保留草稿或清空草稿策略决定是否调用 clearAfterDispose，完成后再启用新实例。跨运行时并发写入仍需 services 的存储协调，本片段仅覆盖单运行时。

若项目选择 Zustand persist，可用 partialize、version／migrate、skipHydration 和恢复状态接口实现同样约束；更换中间件不免除会话校验及写入排空要求。[官方持久化接口](https://zustand.docs.pmnd.rs/reference/integrations/persisting-store-data)

验证：恢复前不能编辑或保存；g1 恢复晚到不能发布状态；旧写入完成并 dispose 后，g2 才恢复同分区；保存拒绝时调用方收到错误；清除发生在旧写入结束后。

<a id="a11"></a>

## A.11 平台实现、类型检查与 Jest 解析

**形式：TypeScript 和工程配置片段。** 用订单页的日期输入提示说明统一契约；完整 DatePicker UI 省略。TypeScript 示例要求支持 moduleSuffixes（TS 5.x 满足）；Jest 的转换链、别名和 RN preset 继承目标工程已有配置。

```ts
// src/components/DatePicker/DatePicker.ios.ts
import type { PickerMode } from './types';
export const pickerMode: PickerMode = 'inline';

// src/components/DatePicker/DatePicker.android.ts
import type { PickerMode } from './types';
export const pickerMode: PickerMode = 'dialog';

// src/components/DatePicker/types.ts
export type PickerMode = 'inline' | 'dialog';

// src/components/DatePicker/index.ts
export { pickerMode } from './DatePicker';
```

这四个段落分别放入四个文件，公开契约为 `pickerMode: 'inline' | 'dialog'`；实际组件也应在共用类型文件声明 props 并让两个实现遵守。调用方只导入 `@components/DatePicker`，不写 `.ios` 或 `.android`。[RN 平台文件机制](https://reactnative.dev/docs/platform-specific-code)

```json
{
  "extends": "./tsconfig.json",
  "compilerOptions": { "moduleSuffixes": [".ios", ".native", ""] },
  "exclude": ["node_modules", "**/*.android.ts", "**/*.android.tsx"]
}
```

目标文件：`tsconfig.ios.json`。Android 配置将 `.ios` 替换成 `.android`，并排除 iOS 实现；实际配置须保留原工程其他排除项。末尾空字符串保留普通文件回退，机制见 [TypeScript moduleSuffixes](https://www.typescriptlang.org/tsconfig/moduleSuffixes.html)。不能让每个平台只检查自己的入口却遗漏它应当包含的共享文件。

```js
// jest.platforms.config.cjs：base 指项目现有可工作的 RN Jest 配置。
const base = require('./jest.config.js');
module.exports = {
  projects: ['ios', 'android'].map((platform) => ({
    ...base,
    displayName: platform,
    haste: { ...base.haste, defaultPlatform: platform, platforms: ['ios', 'android', 'native'] },
    testMatch: ['<rootDir>/src/components/DatePicker/platform.test.ts'],
    globals: { ...base.globals, EXPECTED_PICKER_MODE: platform === 'ios' ? 'inline' : 'dialog' },
  })),
};
```

```ts
// src/components/DatePicker/platform.test.ts
import { expect, test } from '@jest/globals';
import { pickerMode } from './DatePicker';
declare const EXPECTED_PICKER_MODE: 'inline' | 'dialog';
test('选择本平台的实现', () => {
  expect(pickerMode).toBe(EXPECTED_PICKER_MODE);
});
```

```sh
npm exec -- tsc --noEmit -p tsconfig.ios.json
npm exec -- tsc --noEmit -p tsconfig.android.json
npm exec -- jest --config jest.platforms.config.cjs --runInBand
```

Jest 配置依据 [haste.defaultPlatform](https://jestjs.io/docs/configuration#haste-object)。上面是额外的平台解析验收，不替换项目其他测试；若现有 base 已有 projects，则合并子项目而不是再次嵌套 projects。

架构检查器同样需要分别使用 iOS／Android 的解析配置：例如 A.7 的配置按平台选择 tsConfig 与 enhancedResolveOptions.extensions，并核对 Metro 的平台后缀、源扩展名及 package 条件解析顺序。不能仅复制一份 extensions 列表就宣称与 Metro 完全一致。分别保存依赖图并验收以下情形：

| fixture | 预期 |
| --- | --- |
| iOS 的适配器 A 调用 B，Android 的 B 调用 A，各平台另一实现不反向调用 | 各实际平台图无环，不因合并两张图而误报 |
| Android 的 A 与 B 在同一平台互相调用 | Android 检查拒绝 |
| 只有 iOS 实现导入了被矩阵禁止的区域 | iOS 检查拒绝；Android 通过不抵消违规 |

以上循环 fixture 应使用允许同区域公开依赖的 services 模块；不能把本就违反 feature 互引规则的代码作为“无环允许”案例。最终再用受影响平台的 Metro 打包验证实际解析。
