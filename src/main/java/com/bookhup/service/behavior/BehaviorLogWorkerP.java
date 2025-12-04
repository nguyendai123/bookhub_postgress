//package com.bookhup.service.behavior;
//
//import com.bookhup.model.UserBehaviorLog;
//import com.bookhup.repository.UserBehaviorLogRepository;
//import com.bookhup.service.queue.BehaviorLogQueue;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BehaviorLogWorkerP {
//
//    private final BehaviorLogQueue queue;
//    private final UserBehaviorLogRepository repo;
//    private final WeightLearningEngine weightEngine;
//
//    @Value("${behavior-log.workers:1}")
//    private int workers;
//
//    @Value("${behavior-log.batch-size:2}")
//    private int batchSize;
//
//    private final List<Thread> workerThreads = new ArrayList<>();
//    private volatile boolean running = true;
//    private static final UserBehaviorLog POISON = new UserBehaviorLog();
//
//    @PostConstruct
//    public void startWorkers() {
//        for (int i = 0; i < workers; i++) {
//            Thread t = new Thread(this::processLoop, "log-worker-" + i);
//            workerThreads.add(t);
//            t.start();
//        }
//    }
//
//    private void processLoop() {
//        List<UserBehaviorLog> buffer = new ArrayList<>(batchSize);
//
//        while (running) {
//            try {
//                UserBehaviorLog log = queue.take();
//
//                if (log == POISON) break;
//
//                buffer.add(log);
//
//                if (buffer.size() >= batchSize) {
//                    flush(buffer);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // flush cuối
//        flush(buffer);
//    }
//
//    private void flush(List<UserBehaviorLog> buffer) {
//        if (buffer.isEmpty()) return;
//
//        List<UserBehaviorLog> copy = new ArrayList<>(buffer);
//        buffer.clear();
//
//        repo.saveAll(copy);
//        copy.forEach(weightEngine::asyncProcess);
//
//        System.out.println("Flushed " + copy.size() + " logs");
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        System.out.println("Shutting down BehaviorLogWorker...");
//
//        running = false;
//
//        // 1️⃣ Gửi POISON trước để worker thoát take()
//        for (int i = 0; i < workers; i++) {
//            queue.push(POISON);
//        }
//
//        // 2️⃣ Chờ tất cả worker dừng
//        for (Thread t : workerThreads) {
//            try {
//                t.join();
//            } catch (InterruptedException ignored) {}
//        }
//
//        // 3️⃣ Flush queue còn lại (không cần batch)
//        List<UserBehaviorLog> leftover = new ArrayList<>();
//        queue.drainTo(leftover);
//
//        if (!leftover.isEmpty()) {
//            System.out.println("Final leftover flush: " + leftover.size());
//            repo.saveAll(leftover);
//            leftover.forEach(weightEngine::asyncProcess);
//        }
//
//        System.out.println("BehaviorLogWorker stopped safely.");
//    }
//}
