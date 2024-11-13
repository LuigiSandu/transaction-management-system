package project.transaction.management.system.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.user.UserLoginRequestResource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.api.resource.user.UserUpdateRequestResource;
import project.transaction.management.system.config.JWTGenerator;
import project.transaction.management.system.dao.entity.Role;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.RoleRepository;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.UserMapper;

import javax.management.relation.RoleNotFoundException;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final JWTGenerator jwtGenerator;

    @Transactional
    public UserResponseResource createUser(UserRequestResource request) throws RoleNotFoundException {
        validateUserAndEmailUniqueness(request.getUsername(), request.getEmail());

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        final String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);

        final Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role 'USER' not found"));

        final UserEntity userEntity = mapper.toEntity(request);
        userEntity.setRoles(Collections.singletonList(role));
        userRepository.save(userEntity);

        return mapper.fromEntity(userEntity);
    }

    public String loginUser(UserLoginRequestResource request) {
        final UserEntity userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        validatePassword(request.getPassword(), userEntity.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtGenerator.generateToken(userEntity.getId(), userEntity.getTokenVersion());
    }

    @Transactional
    public UserResponseResource updateUser(UserUpdateRequestResource request, String authorizationHeader) {
        String userId = extractUserIdFromToken(authorizationHeader);
        log.debug("Attempting to update user with ID: {}", userId);

        UserEntity existingUser = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        updateUserFields(existingUser, request);

        existingUser.setTokenVersion(existingUser.getTokenVersion() + 1);
        final UserEntity updatedUser = userRepository.save(existingUser);

        return mapper.fromEntity(updatedUser);
    }

    private void validatePassword(String requestPassword, String userPassword) {
        if (!passwordEncoder.matches(requestPassword, userPassword)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    private void updateUserFields(UserEntity user, UserUpdateRequestResource request) {
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private String extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header format");
        }
        return jwtGenerator.getUserIdFromToken(authorizationHeader.substring(7));
    }

    private void validateUserAndEmailUniqueness(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
