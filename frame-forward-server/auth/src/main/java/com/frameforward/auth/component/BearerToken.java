package com.frameforward.auth.component;

/** 仅解析协议内容；无效会话的业务异常由服务入口决定。 */
public final class BearerToken {
    private BearerToken() {
    }
    public static String parse(String authorization) {
        return authorization == null || !authorization.startsWith("Bearer ") ? null : authorization.substring(7);
    }
}
