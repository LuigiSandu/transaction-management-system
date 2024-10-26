package project.transaction.management.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.UserRequestResource;
import project.transaction.management.system.api.resource.UserResponseResource;
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

    private void validateUserUniqueness(String username, String email) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
