package com.cg.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.cg.util.JwtUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        String uri = request.getRequestURI();

        System.out.println("uri: " + uri);

        String headToken = request.getHeader("Authorization");
        System.out.println("Original Authorization header: " + headToken);

        if(StringUtils.isEmpty(headToken)) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 20001);
            map.put("message", "Missing or invalid Authorization header");
            ErrorResponse(response, map);
            return false;
        }

        // 处理 Bearer token
        if (headToken.startsWith("Bearer ")) {
            headToken = headToken.substring(7);
        }

        try {
            Map<String, Claim> map = JwtUtil.verifyToken(headToken);
            System.out.println("Verified token claims: " + map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new HashMap<>();
            map.put("code", 20002);
            map.put("message", "Invalid token: " + e.getMessage());
            ErrorResponse(response, map);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    // 被拦截的请求响应
    private void ErrorResponse(HttpServletResponse response, Map<String, Object> result){
        OutputStream out = null;

        JSONObject object = new JSONObject();
        object.put("result", result);

        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(object.toString().getBytes());
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            try {
                if (out != null) {
                    out.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
