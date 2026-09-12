import { createShootingSessionUseCases } from './ShootingSessionUseCases';

test('创建会话传递方案上下文', async () => {
  const port = {
    createSession: jest.fn().mockResolvedValue({ id: 'session-1', planId: 'plan-1', label: 'x' }),
    getComparison: jest.fn(),
  };
  await expect(
    createShootingSessionUseCases(port).createSession('plan-1', '构图上下文'),
  ).resolves.toMatchObject({
    id: 'session-1',
  });
  expect(port.createSession).toHaveBeenCalledWith({
    shootingPlanId: 'plan-1',
    planContext: '构图上下文',
  });
});

test('重复创建在进行中被拒绝', async () => {
  let finish!: (value: { id: string; planId: string; label: string }) => void;
  const port = {
    createSession: jest.fn().mockReturnValue(new Promise(resolve => (finish = resolve))),
    getComparison: jest.fn(),
  };
  const useCases = createShootingSessionUseCases(port);
  const first = useCases.createSession('plan-1', '上下文');
  await expect(useCases.createSession('plan-1', '上下文')).rejects.toThrow('正在创建');
  finish({ id: 'session-1', planId: 'plan-1', label: 'x' });
  await first;
});
