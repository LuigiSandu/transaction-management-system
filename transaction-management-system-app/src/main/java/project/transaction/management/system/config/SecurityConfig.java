package project.transaction.management.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/transaction-management-system/api/v1/users/register").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/users/login").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/users/update").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/accounts/create").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/accounts/**").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/transactions").permitAll()
                        .requestMatchers("/transaction-management-system/api/v1/users/transactions/**").permitAll()
                        .anyRequest().authenticated() // All other requests need authentication
                )
                .httpBasic(withDefaults()); // Enable HTTP Basic authentication
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt is a strong password encoding algorithm
    }

    @Bean
    public UserDetailsService users() {
        UserDetails admin = User.builder()
                .username("admin")
                .password("password")
                .roles("ADMIN").build();
        UserDetails user = User.builder()
                .username("user")
                .password("password")
                .roles("USER").build();
        return new InMemoryUserDetailsManager(admin, user);

    }

}
