package com.github.zhuyizhuo.datajpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zhuyizhuo.datajpa.model.User;
import com.github.zhuyizhuo.datajpa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllUsers() throws Exception {
        // 准备数据
        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // 执行请求
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                // 验证结果
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[1].username").value("janedoe"));
    }

    @Test
    void getUserByIdFound() throws Exception {
        // 准备数据
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(user1));

        // 执行请求
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                // 验证结果
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUserByIdNotFound() throws Exception {
        // 准备数据
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // 执行请求
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                // 验证结果
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser() throws Exception {
        // 准备数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("new.user@example.com");
        newUser.setFullName("New User");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("newpassword");
        savedUser.setEmail("new.user@example.com");
        savedUser.setFullName("New User");
        savedUser.setCreatedAt(LocalDateTime.now());

        Mockito.when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // 执行请求
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                // 验证结果
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new.user@example.com"));
    }

    @Test
    void updateUserFound() throws Exception {
        // 准备数据
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("updatedpassword");
        updatedUser.setEmail("updated.user@example.com");
        updatedUser.setFullName("Updated User");
        updatedUser.setCreatedAt(LocalDateTime.now());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        Mockito.when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        // 执行请求
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                // 验证结果
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated.user@example.com"));
    }

    @Test
    void updateUserNotFound() throws Exception {
        // 准备数据
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("updatedpassword");
        updatedUser.setEmail("updated.user@example.com");
        updatedUser.setFullName("Updated User");

        Mockito.when(userService.updateUser(anyLong(), any(User.class))).thenThrow(new RuntimeException("用户不存在"));

        // 执行请求
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                // 验证结果
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserFound() throws Exception {
        // 准备数据
        Mockito.doNothing().when(userService).deleteUser(1L);

        // 执行请求
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                // 验证结果
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        // 准备数据
        Mockito.doThrow(new RuntimeException("用户不存在")).when(userService).deleteUser(1L);

        // 执行请求
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                // 验证结果
                .andExpect(status().isNotFound());
    }
}