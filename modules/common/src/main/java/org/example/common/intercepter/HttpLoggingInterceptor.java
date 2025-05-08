package org.example.common.intercepter;

import java.util.Collection;
import java.util.Enumeration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 요청 시작 시간 기록
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append("\n");
        }

        String clientIp = request.getRemoteAddr();

        log.info("==================== REQUEST ====================\nClient IP: {}:{} \nRequest URL: {} \nHTTP Method: {} \n[Headers]: \n{}",
                clientIp,
                request.getRemotePort(),
                request.getRequestURL().toString(),
                request.getMethod(),
                headers.toString()
        );

        response.addHeader("client-ip", clientIp + ":" + request.getRemotePort());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 요청 종료 시간 기록 및 실행 시간 계산
        long startTime = (long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        Collection<String> headerNames = response.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        for (String headerName : headerNames) {
            String headerValue = response.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append("\n");
        }

        log.info("==================== RESPONSE ====================\nStatus: {} \nExecution Time: {} ms \n[Headers]: \n{} \n",
                response.getStatus(),
                executionTime,
                headers.toString()
        );

    }
}
