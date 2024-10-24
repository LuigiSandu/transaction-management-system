package project.transaction.management.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public UserResponseResource createUser(UserRequestResource request) {
        //TODO CHECK USERNAME PASS AND EMAIL? FOR UNIQUNESS
        //TODO SAVE PASSWORD HASHED

        final UserEntity userEntity = mapper.toEntity(request);
        repository.save(userEntity);

        return mapper.fromEntity(userEntity);
    }
}
