package com.sy.model;

import lombok.Data;

@Data
public class AccessToken {
    private String token;
    private String expiresIn;
}
