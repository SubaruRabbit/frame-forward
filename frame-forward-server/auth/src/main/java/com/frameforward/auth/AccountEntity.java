package com.frameforward.auth;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("accounts")
class AccountEntity {
    @TableId String id;
    String username;
    String email;
    String passwordHash;
    AccountEntity() {}
    AccountEntity(String id, String username, String email, String passwordHash) { this.id = id; this.username = username; this.email = email; this.passwordHash = passwordHash; }
    String getUsername() { return username; }
    String getEmail() { return email; }
}
