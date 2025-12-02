package com.bookhup.service.behavior;

import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.UserBehaviorLogRepository;
import com.bookhup.service.queue.BehaviorLogQueue;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BehaviorLogWorker {

    private final BehaviorLogQueue queue;
    private final UserBehaviorLogRepository repo;
    private final WeightLearningEngine weightEngine;

    private static final int BATCH_SIZE = 2;

    @PostConstruct
    public void startWorkers() {
        int workers = Runtime.getRuntime().availableProcessors(); // số core CPU

        for (int i = 0; i < workers; i++) {
            Thread t = new Thread(this::processLoop, "log-worker-" + i);
            t.start();
        }
    }


    private void processLoop() {
        List<UserBehaviorLog> buffer = new ArrayList<>(BATCH_SIZE);

        while (true) {
            try {
                // chờ log
                UserBehaviorLog log = queue.take();
                buffer.add(log);

                // nếu đủ batch thì ghi DB
                if (buffer.size() >= BATCH_SIZE) {
                    repo.saveAll(buffer);       // batch insert siêu nhanh

                    // chạy weightEngine async
                    for (UserBehaviorLog l : buffer) {
                        weightEngine.asyncProcess(l);
                    }

                    buffer.clear();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


