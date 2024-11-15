package project.transaction.management.system;

import project.transaction.management.system.api.resource.user.UserLoginRequestResource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.api.resource.user.UserUpdateRequestResource;
import project.transaction.management.system.dao.entity.UserEntity;

public class AbstractTest {
    private static final String USERNAME = "username1";
    private static final String UPDATED_USERNAME = "newUsername";
    private static final String PASSWORD = "Password@1";
    private static final String UPDATED_PASSWORD = "NewPassword@1";
    public static final String HASHED_PASSWORD = "hashedPassword";
    private static final String EMAIL = "email1@gmail.com";
    private static final String UPDATED_EMAIL = "newEmail@gmail.com";
    public static final String AUTHORIZATION_HEADER = "Bearer mockToken";

    public static UserRequestResource createUserRequestResource() {
        return UserRequestResource.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL)
                .build();
    }

    public static UserEntity createUserEntityResource() {
        return UserEntity.builder()
                .id(1L)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    public static UserResponseResource createUserResponseResource() {
        return UserResponseResource.builder()
                .id("1")
                .username(USERNAME)
                .email(EMAIL).build();
    }

    public static UserLoginRequestResource createUserLoginRequestResource() {
        return UserLoginRequestResource.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    public static UserUpdateRequestResource createUserUpdateRequestResource() {
        return UserUpdateRequestResource.builder()
                .username(UPDATED_USERNAME)
                .email(UPDATED_EMAIL)
                .password(UPDATED_PASSWORD)
                .build();
    }

    public static UserEntity createUserEntityUpdated() {
        return UserEntity.builder()
                .id(1L)
                .username(UPDATED_USERNAME)
                .email(UPDATED_EMAIL)
                .password(UPDATED_PASSWORD)
                .build();
    }

    public static UserResponseResource createUserResponseResourceUpdated() {
        return UserResponseResource.builder()
                .id("1")
                .username(UPDATED_USERNAME)
                .email(UPDATED_EMAIL).build();
    }
}
