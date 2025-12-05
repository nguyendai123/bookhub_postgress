package com.bookhup.service.queue;

import com.bookhup.model.UserBehaviorLog;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BehaviorLogQueue {

    private final BlockingQueue<UserBehaviorLog> queue = new LinkedBlockingQueue<>(50000);

    public void push(UserBehaviorLog log) {
        boolean ok = queue.offer(log);
        if (!ok) {
            System.err.println("BehaviorLogQueue FULL → log dropped");
        }
    }

    public UserBehaviorLog take() throws InterruptedException {
        return queue.take();
    }

    public int drainTo(Collection<? super UserBehaviorLog> target) {
        return queue.drainTo(target);
    }
}

