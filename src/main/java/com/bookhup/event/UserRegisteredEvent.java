package com.bookhup.event;

import com.bookhup.model.User;
import lombok.Getter;

@Getter
public class UserRegisteredEvent {

    private final User user;

    public UserRegisteredEvent(User user) {
        this.user = user;
    }
}

