package com.bookhup.service.behavior;

import com.bookhup.dto.request.like.LikeRequest;
import com.bookhup.model.ActionType;
import com.bookhup.model.User;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.UserBehaviorLogRepository;
import com.bookhup.security.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.bookhup.model.ActionType.*;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class UserBehaviorAspect {

    private final UserBehaviorLogRepository logRepo;
    private final ObjectMapper objectMapper;
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

        // Tự suy luận action theo API + Method
        ActionType action = resolveAction(uri, method, pjp.getArgs());
        Long userId = SecurityUtil.getCurrentUserId();
        // Chạy API thật
        Object result = pjp.proceed();

        // Không cần log → return luôn
        if (action == null) {
            log.info("⏭️ NO LOG ACTION: {} {}", method, uri);
            return result;
        }

        /** ============ Gửi việc log vào BACKGROUND ============ */
        logExecutor.submit(() -> {
            try {
                Map<String, Object> metadata = extractMetadata(pjp.getArgs());
                UserBehaviorLog logItem = UserBehaviorLog.builder()
                        .userId(userId)
                        .actionType(action)
                        .metadata(metadata)
                        .device(SecurityUtil.getDevice(req))
                        .location(SecurityUtil.getLocation(req))
                        .timestamp(LocalDateTime.now())
                        .build();

                logRepo.save(logItem);

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
                (at == POST_LIKE || at == BOOKREVIEW_LIKE || at == COMMENT_LIKE)) {

                String target = likeReq.getTargetType();

                if ("POST".equalsIgnoreCase(target)) return POST_LIKE;
                if ("BOOKREVIEW".equalsIgnoreCase(target)) return BOOKREVIEW_LIKE;
                if ("COMMENT".equalsIgnoreCase(target)) return COMMENT_LIKE;
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
    private Map<String, Object> extractMetadata(Object[] args) {

        Map<String, Object> map = new HashMap<>();

        int index = 0;
        for (Object arg : args) {

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

            try {
                // Nếu là DTO → convert sang map (nhỏ, gọn)
                map.put("arg" + index, objectMapper.convertValue(arg, Map.class));
            } catch (Exception e) {
                // fallback nếu convert fail
                map.put("arg" + index, arg.toString());
            }

            index++;
        }

        return map;
    }

    private boolean isIgnoredType(Object arg) {
        return arg instanceof org.springframework.core.io.Resource
               || arg instanceof org.springframework.web.multipart.MultipartFile
               || arg instanceof byte[]
               || arg.getClass().getName().startsWith("org.springframework");
    }

}

