package com.bookhup.service.behavior;

import com.bookhup.dto.request.like.LikeRequest;
import com.bookhup.dto.response.auth.AuthResponse;
import com.bookhup.model.ActionType;
import com.bookhup.model.User;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.UserBehaviorLogRepository;
import com.bookhup.security.SecurityUtil;
import com.bookhup.service.notification.NotificationBatchWorker;
import com.bookhup.service.notification.TargetUserResolver;
import com.bookhup.service.queue.BehaviorLogQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static com.bookhup.model.ActionType.*;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class UserBehaviorAspect {

    private final UserBehaviorLogRepository logRepo;
    private final ObjectMapper objectMapper;
    private final BehaviorLogQueue logQueue;
    private final NotificationBatchWorker notificationWorker;
    private final TargetUserResolver targetUserResolver;

    /**
     * ---------------- THREAD POOL CỰC NHẸ CHO LOGGING ----------------
     */
    private final ExecutorService logExecutor;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object autoLog(ProceedingJoinPoint pjp) throws Throwable {

        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr == null) return pjp.proceed();   // Không có HTTP request → bỏ qua

        HttpServletRequest req = attr.getRequest();

        String uri = req.getRequestURI();
        String method = req.getMethod();
        //Lấy device + location NGAY TRONG REQUEST THREAD
        String device = SecurityUtil.getDevice(req);
        String location = SecurityUtil.getLocation(req);

        // 👇 Chạy API thật
        Object result = pjp.proceed();

        AtomicReference<Long> finalCurrentUserId = new AtomicReference<>();
        AtomicReference<String> finalCurrentUserName = new AtomicReference<>();

        // Lấy từ Security (nếu có)
        User user = SecurityUtil.getCurrentUser();
        if (user != null) {
            finalCurrentUserId.set(user.getUserId());
            finalCurrentUserName.set(user.getUsername());
        }

        /** ============ Gửi việc log vào BACKGROUND ============ */
        logExecutor.submit(() -> {
            try {
                // Tự suy luận action theo API + Method
                ActionType action = resolveAction(uri, method, pjp.getArgs());
                // Không cần log → return luôn
                if (action == null) {
                    log.info("⏭️ NO LOG ACTION: {} {}", method, uri);
                    return;
                }
                // Nếu login, Security không có → lấy từ token hoặc return value
                if (finalCurrentUserId.get() == null) {

                    // 1) Trường hợp result là Map
                    if (result instanceof Map<?, ?> map && map.containsKey("userId")) {
                        finalCurrentUserId.set(Long.parseLong(map.get("userId").toString()));
                    }

                    // 2) Trường hợp result là ResponseEntity<AuthResponse>
                    else if (result instanceof ResponseEntity<?> res
                             && res.getBody() instanceof AuthResponse auth) {

                        finalCurrentUserId.set(auth.getUserId());
                        finalCurrentUserName.set(auth.getUsername());
                    }
                }

                Map<String, Object> metadata = extractMetadata(pjp.getArgs(), pjp);
                Long target = targetUserResolver.resolve(action, uri, metadata, finalCurrentUserId.get());

                var log = UserBehaviorLog.builder()
                        .userId(finalCurrentUserId.get())
                        .username(finalCurrentUserName.get())
                        .targetUserId(target)
                        .actionType(action)
                        .metadata(metadata)
                        .device(device)
                        .location(location)
                        .timestamp(LocalDateTime.now())
                        .build();
                // 1) Ghi log hành vi
                logQueue.push(log);
                // 2) Gửi thông báo nếu cần
                notificationWorker.submit(log);
            } catch (Exception e) {
                log.error("Failed to save user behavior log", e);
            }
        });
        return result;
    }

    /**
     * ================== AUTO MATCH API → ACTION =====================
     **/
    private ActionType resolveAction(String uri, String method, Object[] args) {

        Object body = findRequestBody(args, LikeRequest.class);
        LikeRequest likeReq = body instanceof LikeRequest ? (LikeRequest) body : null;

        for (ActionType at : ActionType.values()) {

            if (!at.getMethod().name().equals(method)) continue;
            if (!at.getCompiledPattern().matcher(uri).matches()) continue;

            // Special: POST /api/like → LikeRequest trong body
            if (likeReq != null &&
                Set.of(POST_LIKE, BOOKREVIEW_LIKE, COMMENT_LIKE, POST_UNLIKE, BOOKREVIEW_UNLIKE, COMMENT_UNLIKE).contains(at)) {

                String target = likeReq.getTargetType();

                return switch (target.toUpperCase()) {
                    case "POST" -> at == COMMENT_UNLIKE ? POST_UNLIKE : POST_LIKE;
                    case "BOOKREVIEW" -> at == COMMENT_UNLIKE ? BOOKREVIEW_UNLIKE : BOOKREVIEW_LIKE;
                    case "COMMENT" -> at == COMMENT_UNLIKE ? COMMENT_UNLIKE : COMMENT_LIKE;
                    default -> at; // fallback
                };
            }

            return at;
        }

        return null;
    }

    private Object findRequestBody(Object[] args, Class<?> type) {
        for (Object arg : args) {
            if (arg != null && type.isAssignableFrom(arg.getClass())) {
                return arg;
            }
        }
        return null;
    }

    /**
     * ================== Metadata =====================
     **/
    private Map<String, Object> extractMetadata(Object[] args, ProceedingJoinPoint pjp) {

        Map<String, Object> map = new HashMap<>();

        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String[] paramNames = sig.getParameterNames();   // <-- tên thật trong method
        Class<?>[] paramTypes = sig.getParameterTypes();

        int index = 0;
        for (int i = 0; i < args.length; i++) {

            Object arg = args[i];
            String paramName = paramNames[i];
            Class<?> type = paramTypes[i];

            if (arg == null) continue;

            // Loại bỏ Request/Response
            if (arg instanceof HttpServletRequest ||
                arg instanceof HttpServletResponse) {
                continue;
            }

            // Loại bỏ User đầy đủ – chỉ log userId (đã lấy từ token)
            if (arg instanceof User) {
                continue;   // vì bạn đã có userId rồi
            }

            // Loại bỏ BindingResult
            if (arg.getClass().getSimpleName().equals("BindingResult")) {
                continue;
            }

            // Loại bỏ các object bạn không muốn log toàn bộ
            if (isIgnoredType(arg)) {
                continue;
            }

            // ✔ 3. Nếu là primitive hoặc wrapper → lưu thẳng
            if (isPrimitiveLike(type)) {
                map.put(paramName, arg);
                continue;
            }

            // ✔ 4. Nếu là String → lưu luôn
            if (arg instanceof String) {
                map.put(paramName, arg);
                continue;
            }

            // ✔ 5. Nếu là DTO → chuyển sang map bằng ObjectMapper
            try {
                Map<String, Object> dtoMap =
                        objectMapper.convertValue(arg, Map.class);
                map.put(paramName, dtoMap);
            } catch (Exception e) {
                // fallback
                map.put(paramName, arg.toString());
            }
        }

        return map;
    }

    /**
     * Kiểm tra primitive / wrapper / Long / Integer / Boolean
     */
    private boolean isPrimitiveLike(Class<?> type) {
        return type.isPrimitive()
               || Number.class.isAssignableFrom(type)
               || type == Boolean.class
               || type == Character.class;
    }

    private boolean isIgnoredType(Object arg) {
        return arg instanceof org.springframework.core.io.Resource
               || arg instanceof org.springframework.web.multipart.MultipartFile
               || arg instanceof byte[]
               || arg.getClass().getName().startsWith("org.springframework");
    }

}

