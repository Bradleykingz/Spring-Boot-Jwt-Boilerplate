package com.retroshift.springjwtauthboilerplate.spring.filter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.retroshift.springjwtauthboilerplate.pojo.UserPojo;
import com.retroshift.springjwtauthboilerplate.spring.persistence.UserService;
import com.retroshift.springjwtauthboilerplate.utils.JwtUtils;
import com.retroshift.springjwtauthboilerplate.utils.MainUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JwtAuthenticationFilter extends CustomUsernamePasswordAutheticationFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    private AuthenticationManager authenticationManager;

    @Autowired
    private Gson gson;

    private UserPojo userPojo;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.setAuthenticationManager(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
            String plan = scanner.hasNext() ? scanner.next() : "";

            try {
                userPojo = gson.fromJson(plan, UserPojo.class);
            } catch (IllegalStateException | JsonSyntaxException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", e.getMessage());
                error.put("code", 400);

                MainUtil.writeToResponseBody(error, response);
                return null;
            }

            if (userPojo != null && !StringUtils.isEmpty(userPojo.getEmail()) && !StringUtils.isEmpty(userPojo.getPassword())) {
                logger.info("User was not null");
                try {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    userPojo.getEmail(),
                                    userPojo.getPassword(),
                                    new ArrayList<>()
                            )
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return authentication;
                } catch (Exception e) {

                    String message = e.getMessage();
                    if (StringUtils.isEmpty(message)) {
                        message = "No user with that email address exists";
                    } else if (message.equalsIgnoreCase("Bad credentials")) {
                        message = "Invalid username/email password combination";
                    }
                    throw new UsernameNotFoundException(message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Missing username/email or password");
            error.put("code", 400);
            MainUtil.writeToResponseBody(error, response);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
