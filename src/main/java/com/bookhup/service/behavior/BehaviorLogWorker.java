package com.bookhup.service.behavior;

import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.UserBehaviorLogRepository;
import com.bookhup.service.queue.BehaviorLogQueue;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BehaviorLogWorker {

    private final BehaviorLogQueue queue;
    private final UserBehaviorLogRepository repo;
    private final WeightLearningEngine weightEngine;

    @Value("${behavior-log.workers:1}")
    private int workers;

    @Value("${behavior-log.batch-size:2}")
    private int batchSize;

    @PostConstruct
    public void startWorkers() {
        for (int i = 0; i < workers; i++) {
            Thread t = new Thread(this::processLoop, "log-worker-" + i);
            t.start();
        }
    }


    private void processLoop() {
        List<UserBehaviorLog> buffer = new ArrayList<>(batchSize);

        while (true) {
            try {
                // chờ log
                UserBehaviorLog log = queue.take();
                buffer.add(log);

                // nếu đủ batch thì ghi DB
                if (buffer.size() >= batchSize) {
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


