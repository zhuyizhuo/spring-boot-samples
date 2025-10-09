package com.github.zhuyizhuo.datajpa.service;

import com.github.zhuyizhuo.datajpa.model.User;
import com.github.zhuyizhuo.datajpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("johndoe");
        user1.setPassword("password123");
        user1.setEmail("john.doe@example.com");
        user1.setFullName("John Doe");
        user1.setCreatedAt(LocalDateTime.now());

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("janedoe");
        user2.setPassword("password456");
        user2.setEmail("jane.doe@example.com");
        user2.setFullName("Jane Doe");
        user2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllUsers() {
        // 准备数据
        List<User> userList = Arrays.asList(user1, user2);
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        // 执行方法
        List<User> result = userService.getAllUsers();

        // 验证结果
        assertEquals(2, result.size());
        assertEquals("johndoe", result.get(0).getUsername());
        assertEquals("janedoe", result.get(1).getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getUserByIdFound() {
        // 准备数据
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // 执行方法
        Optional<User> result = userService.getUserById(1L);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("johndoe", result.get().getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getUserByIdNotFound() {
        // 准备数据
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行方法
        Optional<User> result = userService.getUserById(1L);

        // 验证结果
        assertFalse(result.isPresent());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getUserByUsernameFound() {
        // 准备数据
        Mockito.when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user1));

        // 执行方法
        Optional<User> result = userService.getUserByUsername("johndoe");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername("johndoe");
    }

    @Test
    void getUserByUsernameNotFound() {
        // 准备数据
        Mockito.when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // 执行方法
        Optional<User> result = userService.getUserByUsername("nonexistentuser");

        // 验证结果
        assertFalse(result.isPresent());
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername("nonexistentuser");
    }

    @Test
    void createUserSuccess() {
        // 准备数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("new.user@example.com");
        newUser.setFullName("New User");

        Mockito.when(userRepository.existsByUsername("newuser")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("new.user@example.com")).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(newUser);

        // 执行方法
        User result = userService.createUser(newUser);

        // 验证结果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new.user@example.com", result.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("newuser");
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail("new.user@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).save(newUser);
    }

    @Test
    void createUserUsernameExists() {
        // 准备数据
        User newUser = new User();
        newUser.setUsername("johndoe");
        newUser.setPassword("newpassword");
        newUser.setEmail("new.user@example.com");
        newUser.setFullName("New User");

        Mockito.when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(newUser);
        });

        // 验证结果
        assertEquals("用户名已存在: johndoe", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("johndoe");
        Mockito.verify(userRepository, Mockito.never()).existsByEmail(anyString());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void createUserEmailExists() {
        // 准备数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("john.doe@example.com");
        newUser.setFullName("New User");

        Mockito.when(userRepository.existsByUsername("newuser")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(newUser);
        });

        // 验证结果
        assertEquals("邮箱已存在: john.doe@example.com", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("newuser");
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail("john.doe@example.com");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void updateUserSuccess() {
        // 准备数据
        User updatedUserDetails = new User();
        updatedUserDetails.setUsername("updateduser");
        updatedUserDetails.setPassword("updatedpassword");
        updatedUserDetails.setEmail("updated.user@example.com");
        updatedUserDetails.setFullName("Updated User");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("updated.user@example.com")).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

        // 执行方法
        User result = userService.updateUser(1L, updatedUserDetails);

        // 验证结果
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated.user@example.com", result.getEmail());
        assertEquals("Updated User", result.getFullName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("updateduser");
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail("updated.user@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).save(user1);
    }

    @Test
    void updateUserNotFound() {
        // 准备数据
        User updatedUserDetails = new User();
        updatedUserDetails.setUsername("updateduser");
        updatedUserDetails.setPassword("updatedpassword");
        updatedUserDetails.setEmail("updated.user@example.com");
        updatedUserDetails.setFullName("Updated User");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updatedUserDetails);
        });

        // 验证结果
        assertEquals("用户不存在: 1", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.never()).existsByUsername(anyString());
        Mockito.verify(userRepository, Mockito.never()).existsByEmail(anyString());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void updateUserUsernameExists() {
        // 准备数据
        User updatedUserDetails = new User();
        updatedUserDetails.setUsername("janedoe"); // 这是user2的用户名
        updatedUserDetails.setPassword("updatedpassword");
        updatedUserDetails.setEmail("updated.user@example.com");
        updatedUserDetails.setFullName("Updated User");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.existsByUsername("janedoe")).thenReturn(true);

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updatedUserDetails);
        });

        // 验证结果
        assertEquals("用户名已存在: janedoe", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("janedoe");
        Mockito.verify(userRepository, Mockito.never()).existsByEmail(anyString());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void updateUserEmailExists() {
        // 准备数据
        User updatedUserDetails = new User();
        updatedUserDetails.setUsername("updateduser");
        updatedUserDetails.setPassword("updatedpassword");
        updatedUserDetails.setEmail("jane.doe@example.com"); // 这是user2的邮箱
        updatedUserDetails.setFullName("Updated User");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updatedUserDetails);
        });

        // 验证结果
        assertEquals("邮箱已存在: jane.doe@example.com", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("updateduser");
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail("jane.doe@example.com");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void deleteUserSuccess() {
        // 准备数据
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.doNothing().when(userRepository).delete(user1);

        // 执行方法
        userService.deleteUser(1L);

        // 验证结果
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user1);
    }

    @Test
    void deleteUserNotFound() {
        // 准备数据
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });

        // 验证结果
        assertEquals("用户不存在: 1", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.never()).delete(any(User.class));
    }
}