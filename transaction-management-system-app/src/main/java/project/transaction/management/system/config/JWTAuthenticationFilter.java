package project.transaction.management.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import project.transaction.management.system.exception.ExceptionResponse;

import java.io.IOException;
import java.util.UUID;

import static project.transaction.management.system.config.SecurityConstants.LOGIN_PATH;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTGenerator tokenGenerator;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTFromRequest(request);
        if (StringUtils.hasText(token) || (!request.getRequestURI().contains(LOGIN_PATH))) {
            try {
                if (tokenGenerator.validateToken(token)) {
                    String username = tokenGenerator.getUsernameFromJWT(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (AuthenticationCredentialsNotFoundException ex) {
                handleAuthenticationCredentialsNotFoundException(ex, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex, HttpServletResponse response) {
        // Log the error
        log.info("Authentication error: {}", ex.getMessage(), ex);

        // Create the ExceptionResponse object
        String errorId = UUID.randomUUID().toString(); // Generate a unique ID for the error response
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code("AUTH_CREDENTIALS_NOT_FOUND") // Custom error code for clarity
                .message("Invalid or missing JWT token") // Message from the exception
                .id(errorId) // Unique error ID
                .build();

        try {
            // Convert the exception response to JSON
            String jsonResponse = objectMapper.writeValueAsString(exceptionResponse);

            // Set response properties
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } catch (IOException ioException) {
            log.error("Failed to write response: {}", ioException.getMessage(), ioException);
        }
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(("Bearer "))) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
