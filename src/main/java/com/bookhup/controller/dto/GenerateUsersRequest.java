package com.bookhup.controller.dto;

import lombok.Data;

@Data
public class GenerateUsersRequest {
    private int count;          // số lượng user cần tạo
    private String password;    // password chung cho dễ test
}
