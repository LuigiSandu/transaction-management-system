package project.transaction.management.system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.transaction.management.system.service.helper.AbstractTest.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Role role = Role.builder().name("USER").id(1L).build();

    @Mock
    private UserMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JWTGenerator jwtGenerator;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("Create User Successfully")
    void createUser_Success() throws RoleNotFoundException, RoleNotFoundException {
        final UserRequestResource request = createUserRequestResource();
        final UserResponseResource expectedResponse = createUserResponseResource();
        final UserEntity userEntity = createUserEntityResource();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(mapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.fromEntity(userEntity)).thenReturn(expectedResponse);

        final UserResponseResource actualResponse = userService.createUser(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getUsername(), actualResponse.getUsername());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Fail to Create User - Username Exists")
    void createUser_Fail_UsernameExists() {
        final UserRequestResource request = createUserRequestResource();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Fail to Create User - Email Exists")
    void createUser_Fail_EmailExists() {
        final UserRequestResource request = createUserRequestResource();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Fail to Create User - Role Not Found")
    void createUser_Fail_RoleNotFound() {
        final UserRequestResource request = createUserRequestResource();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(HASHED_PASSWORD);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        final Exception exception = assertThrows(RoleNotFoundException.class, () -> userService.createUser(request));
        assertEquals("Role 'USER' not found", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Login User Successfully")
    void loginUser_Success() {
        final UserLoginRequestResource loginRequest = createUserLoginRequestResource();
        final UserEntity userEntity = createUserEntityResource();
        final String expectedToken = "mockJwtToken";

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())).thenReturn(true);
        final Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtGenerator.generateToken(userEntity.getId(), userEntity.getTokenVersion())).thenReturn(expectedToken);

        final String actualToken = userService.loginUser(loginRequest);

        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtGenerator, times(1)).generateToken(userEntity.getId(), userEntity.getTokenVersion());
    }

    @Test
    @DisplayName("Fail to Login - Username Not Found")
    void loginUser_Fail_UsernameNotFound() {
        final UserLoginRequestResource loginRequest = createUserLoginRequestResource();

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(loginRequest));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtGenerator, never()).generateToken(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Fail to Login - Password Mismatch")
    void loginUser_Fail_PasswordMismatch() {
        final UserLoginRequestResource loginRequest = createUserLoginRequestResource();
        final UserEntity userEntity = createUserEntityResource();

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(loginRequest));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtGenerator, never()).generateToken(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Update User Successfully")
    void updateUser_Success() {
        final String userId = "1";
        final UserUpdateRequestResource updateRequest = createUserUpdateRequestResource();
        final UserEntity existingUser = createUserEntityResource();
        existingUser.setId(Long.valueOf(userId));
        final UserEntity updatedUser = createUserEntityUpdated();
        final UserResponseResource expectedResponse = createUserResponseResourceUpdated();

        when(jwtGenerator.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(existingUser));
        when(userRepository.saveAndFlush(existingUser)).thenReturn(updatedUser);
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn(updateRequest.getPassword());
        when(mapper.fromEntity(updatedUser)).thenReturn(expectedResponse);

        UserResponseResource actualUpdatedUser = userService.updateUser(updateRequest, AUTHORIZATION_HEADER);

        assertNotNull(actualUpdatedUser);
        assertEquals(updatedUser.getUsername(), actualUpdatedUser.getUsername());
        assertEquals(updatedUser.getEmail(), actualUpdatedUser.getEmail());
        verify(userRepository, times(1)).saveAndFlush(existingUser);
    }

    @Test
    @DisplayName("Fail to Update User - User Not Found")
    void updateUser_Fail_UserNotFound() {

        final String userId = "1";
        final UserUpdateRequestResource updateRequest = createUserUpdateRequestResource();

        when(jwtGenerator.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.empty());

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(updateRequest, AUTHORIZATION_HEADER));
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(Long.valueOf(userId));
    }
}
