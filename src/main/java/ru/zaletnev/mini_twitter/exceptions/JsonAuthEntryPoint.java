package ru.zaletnev.mini_twitter.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.zaletnev.mini_twitter.model.dto.ErrorResponse;

import java.io.IOException;
import java.time.Instant;

@Component
public class JsonAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                "UNATHORIZED",
                "AUTH IS NEEDED",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI(),
                Instant.now()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), errorResponse);
    }
}
