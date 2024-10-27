package project.transaction.management.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.user.UserLoginRequestResource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.api.resource.user.UserUpdateRequestResource;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.UserMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseResource createUser(UserRequestResource request) {
        //TODO CHECK USERNAME PASS AND EMAIL? FOR UNIQUNESS
        validateUserUniqueness(request.getUsername(), request.getEmail());
        //TODO SAVE PASSWORD HASHED
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);

        final UserEntity userEntity = mapper.toEntity(request);
        repository.save(userEntity);

        return mapper.fromEntity(userEntity);
    }

    public UserResponseResource loginUser(UserLoginRequestResource request) {
        final UserEntity userEntity = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Verify the password
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Optional: Generate an auth token (if implementing JWT) here
        // String token = generateAuthToken(userEntity); // Implement token generation if needed

        return mapper.fromEntity(userEntity); // You can also add the token to this response if needed
    }

    public UserResponseResource updateUser(UserUpdateRequestResource request) {
        Long userId = request.getUserId(); // Assume userId is included in the request

        // Retrieve the existing user by ID
        UserEntity existingUser = repository.findById(userId)
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
        UserEntity updatedUser = repository.save(existingUser);

        return mapper.fromEntity(updatedUser);
    }


    private void validateUserUniqueness(String username, String email) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
