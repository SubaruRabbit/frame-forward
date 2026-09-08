package com.frameforward.auth.gateway;

/** 账号注销前必须完成的账户私有文件清理端口。 */
public interface AccountDataCleanup {
    void deleteForAccount(String accountId);
}
