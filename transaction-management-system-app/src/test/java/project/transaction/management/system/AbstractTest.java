package project.transaction.management.system;

import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountUpdateRequest;
import project.transaction.management.system.api.resource.user.UserLoginRequestResource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.api.resource.user.UserUpdateRequestResource;
import project.transaction.management.system.dao.entity.AccountEntity;
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
    public static final String AUTHENTICATED_USERNAME = "username1";
    public static final String ACCOUNT_NUMBER = "12345";

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
    public static AccountRequestResource createAccountRequestResource() {
        return AccountRequestResource.builder()
                .accountNumber("12345")
                .name("Test Account")
                .balance(1000.0)
                .build();
    }

    public static AccountEntity createAccountEntityResource() {
        return AccountEntity.builder()
                .id(1L)
                .accountNumber("12345")
                .name("Test Account")
                .balance(1000.0)
                .user(createUserEntityResource())
                .build();
    }

    public static AccountEntity createAccountEntityUpdated() {
        return AccountEntity.builder()
                .id(1L)
                .accountNumber("12345")
                .name("Updated Account Name")
                .balance(1500.0)
                .user(createUserEntityResource())
                .build();
    }

    public static AccountResponseResource createAccountResponseResource() {
        return AccountResponseResource.builder()
                .accountNumber("12345")
                .name("Test Account")
                .balance(1000.0)
                .build();
    }
    public static AccountResponseResource createAccountResponseResourceUpdated() {
        return AccountResponseResource.builder()
                .accountNumber("12345")
                .name("Updated Account Name")
                .balance(1000.0)
                .build();
    }

    public static AccountUpdateRequest createAccountUpdateRequestResource() {
        return AccountUpdateRequest.builder()
                .name("Updated Account Name")
                .build();
    }

}
