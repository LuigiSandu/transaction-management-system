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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.exception.ExceptionResponse;

import java.io.IOException;
import java.util.UUID;

import static project.transaction.management.system.config.SecurityConstants.*;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTGenerator tokenGenerator;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTFromRequest(request);

        if (StringUtils.hasText(token) || ((!request.getRequestURI().contains(LOGIN_PATH)) &&
                (!request.getRequestURI().contains(ACTUATOR)) &&
                (!request.getRequestURI().contains(FAVICON)) && (!request.getRequestURI().contains(REGISTER_PATH)))) {
            try {
                if (tokenGenerator.validateToken(token)) {
                    String id = tokenGenerator.getSubjectFromJwt(token);
                    int tokenVersion = tokenGenerator.getTokenVersionFromJwt(token);

                    UserDetails userDetails = customUserDetailsService.loadUserById(id);

                    UserEntity userEntity = userRepository.findById(Long.valueOf(id))
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                    if (userEntity.getTokenVersion() != tokenVersion) {
                        throw new AuthenticationCredentialsNotFoundException("Token version mismatch. Please re-authenticate.");
                    }

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (AuthenticationCredentialsNotFoundException | UsernameNotFoundException ex) {
                handleJwtException(ex, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(Exception ex, HttpServletResponse response) {
        log.info("Authentication error: {}", ex.getMessage(), ex);

        String errorId = UUID.randomUUID().toString();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code("AUTH_CREDENTIALS_NOT_FOUND")
                .message("Invalid or missing JWT token")
                .id(errorId)
                .build();

        try {
            String jsonResponse = objectMapper.writeValueAsString(exceptionResponse);

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
