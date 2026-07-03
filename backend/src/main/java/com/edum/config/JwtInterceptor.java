package com.edum.config;

import com.edum.common.Result;
import com.edum.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Token
        String token = request.getHeader("Authorization");
        
        // Token不存在或格式不正确
        if (token == null || !token.startsWith("Bearer ")) {
            sendError(response, 401, "未提供有效的Token");
            return false;
        }
        
        // 提取Token（去掉"Bearer "前缀）
        token = token.substring(7);
        
        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            sendError(response, 401, "Token无效或已过期");
            return false;
        }
        
        // 从Token中解析用户ID，放入request attribute
        Long userId = JwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        
        return true;
    }
    
    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Void> result = Result.error(code, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
