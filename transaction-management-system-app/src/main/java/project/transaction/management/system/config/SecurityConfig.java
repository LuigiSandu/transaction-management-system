package project.transaction.management.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection for development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/transaction-management-system/api/v1/users/register").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/users/login").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/users/**").authenticated() // Require authentication
                        .anyRequest().authenticated()  // All other requests need authentication
                )
                .httpBasic(withDefaults());  // Enable HTTP Basic authentication
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt is a strong password encoding algorithm
    }
}
