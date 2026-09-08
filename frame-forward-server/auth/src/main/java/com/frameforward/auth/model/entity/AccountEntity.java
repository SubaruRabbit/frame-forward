package com.frameforward.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("accounts")
public class AccountEntity {
    @TableId
    public String id;
    public String username;
    public String email;
    public String passwordHash;
    public AccountEntity() {
    }
    public AccountEntity(String id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
