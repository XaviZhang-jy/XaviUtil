package com.example.Util.Mybatis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserLogin {
    private String username;
    private String password;
}