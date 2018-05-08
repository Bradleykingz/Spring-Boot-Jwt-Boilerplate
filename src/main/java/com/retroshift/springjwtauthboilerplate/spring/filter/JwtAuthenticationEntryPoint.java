package com.retroshift.springjwtauthboilerplate.spring.filter;

import com.retroshift.springjwtauthboilerplate.utils.MainUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("reason", "You're not authorized to do that");
        map.put("code", 403);

        MainUtil.writeToResponseBody(map, response);
    }
}
