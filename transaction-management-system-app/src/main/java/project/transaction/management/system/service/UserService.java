package project.transaction.management.system.service;

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

    public UserResponseResource createUser(UserRequestResource request) throws RoleNotFoundException {
        validateUserAndEmailUniqueness(request.getUsername(), request.getEmail());
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

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Optional: Generate an auth token (if implementing JWT) here
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // String token = generateAuthToken(userEntity); // Implement token generation if needed
        ;

        return jwtGenerator.generateToken(userEntity.getId(), userEntity.getTokenVersion()); // You can also add the token to this response if needed
    }

    public UserResponseResource updateUser(UserUpdateRequestResource request) {
        Long userId = request.getUserId(); // Assume userId is included in the request

        // Retrieve the existing user by ID
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Update the existing user fields with values from the request
        if (request.getUsername() != null) {
            existingUser.setUsername(request.getUsername()); // Set new username
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail()); // Set new email
        }
        if (request.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encode new password
        }

        // Save the updated user entity
        existingUser.setTokenVersion(existingUser.getTokenVersion() + 1);
        UserEntity updatedUser = userRepository.save(existingUser);
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return mapper.fromEntity(updatedUser);
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
