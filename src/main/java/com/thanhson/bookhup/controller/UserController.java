package com.thanhson.bookhup.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.response.ResponseSuccess;
import com.thanhson.bookhup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<ResponseSuccess<User>> updateUser(@PathVariable("id") Long id,
            @RequestBody User userInfoRequest) {
        User userResponse = userService.updateUser(id, userInfoRequest);
        ResponseSuccess<User> responseSuccess = new ResponseSuccess<>();
        responseSuccess.setMessage("Updated user successfully.");
        responseSuccess.setData(userResponse);
        return ResponseEntity.ok(responseSuccess);
    }
}
