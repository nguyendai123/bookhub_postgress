package com.bookhup.service.queue;

import com.bookhup.model.UserBehaviorLog;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BehaviorLogQueue {

    private final BlockingQueue<UserBehaviorLog> queue = new LinkedBlockingQueue<>(50000);

    public void push(UserBehaviorLog log) {
        queue.offer(log);
    }

    public UserBehaviorLog take() throws InterruptedException {
        return queue.take();
    }
}

