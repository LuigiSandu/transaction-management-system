package project.transaction.management.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.UserRequestResource;
import project.transaction.management.system.api.resource.UserResponseResource;
import project.transaction.management.system.mapper.UserMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;

    public UserResponseResource createUser(UserRequestResource request){
        //check if user or pass already exists...
        UserResponseResource response = mapper.fromUserRequestResource(request);
        return response;
    }
}
