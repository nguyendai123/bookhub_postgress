package com.bookhup.controller.generate_data.dto;

import lombok.Data;

@Data
public class GenerateUsersRequest {
    private int count;          // số lượng user cần tạo
    private String password;    // password chung cho dễ test
}
