package com.bookhup.security;

import com.bookhup.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * Lấy userId từ SecurityContext (principal = User)
     */
    public static User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // principal là User vì bạn set trong Filter
        if (principal instanceof User user) {
            return user;
        }

        return null; // hoặc throw exception tùy bạn
    }
    /**
     * Lấy IP thực sự của client kể cả khi chạy sau Proxy / Nginx
     */
    public static String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "CF-Connecting-IP",
                "X-Client-IP",
                "X-Remote-IP"
        };

        for (String h : headers) {
            String ip = request.getHeader(h);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Nếu có nhiều IP => lấy IP đầu tiên
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * Xác định thiết bị dựa trên User-Agent
     */
    public static String getDevice(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) return "UNKNOWN";

        ua = ua.toLowerCase();

        if (ua.contains("mobile") || ua.contains("iphone") || ua.contains("android"))
            return "MOBILE";

        if (ua.contains("ipad") || ua.contains("tablet"))
            return "TABLET";

        return "DESKTOP";
    }

    /**
     * Lấy thông tin địa điểm dựa trên IP
     * Nếu không gọi API geo → chỉ trả về IP
     */
    public static String getLocation(HttpServletRequest request) {

        // ❗ Nếu bạn KHÔNG dùng geo API, trả về IP
        return getClientIp(request);

        /*
        // Nếu muốn mở rộng → dùng IP-API.com /ipinfo.io
        try {
            String json = new RestTemplate().getForObject(
                    "http://ip-api.com/json/" + ip + "?fields=city,country,query",
                    String.class
            );
            return json; // hoặc parse ra city + country
        } catch (Exception ex) {
            return ip; // fallback
        }
        */
    }
}
