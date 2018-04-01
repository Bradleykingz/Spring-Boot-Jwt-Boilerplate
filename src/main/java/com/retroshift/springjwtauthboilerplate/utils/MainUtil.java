package com.retroshift.springjwtauthboilerplate.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class MainUtil {
    public static void writeToResponseBody(Map<Object, Object> o,
                                           HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus((Integer) o.get("code"));

        PrintWriter printWriter = response.getWriter();
        objectMapper.writeValue(printWriter, o);
        printWriter.flush();
        printWriter.close();

    }
}
