package com.example.mapstructdemo.service;

import com.example.mapstructdemo.dto.UserDTO;
import com.example.mapstructdemo.entity.UserEntity;
import com.example.mapstructdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final Map<Long, UserEntity> userRepository = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // 初始化一些示例数据
    public UserService() {
        UserEntity user1 = new UserEntity();
        user1.setId(idGenerator.getAndIncrement());
        user1.setUsername("张三");
        user1.setPassword("password123");
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800138001");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        user1.setActive(true);

        UserEntity user2 = new UserEntity();
        user2.setId(idGenerator.getAndIncrement());
        user2.setUsername("李四");
        user2.setPassword("password456");
        user2.setEmail("lisi@example.com");
        user2.setPhone("13900139002");
        user2.setBirthDate(LocalDate.of(1995, 5, 15));
        user2.setActive(false);

        userRepository.put(user1.getId(), user1);
        userRepository.put(user2.getId(), user2);
    }

    // 获取所有用户
    public List<UserDTO> getAllUsers() {
        List<UserEntity> userEntities = new ArrayList<>(userRepository.values());
        return userMapper.toDTOList(userEntities);
    }

    // 根据ID获取用户
    public UserDTO getUserById(Long id) {
        UserEntity userEntity = userRepository.get(id);
        return userEntity != null ? userMapper.toDTO(userEntity) : null;
    }

    // 创建用户
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = userMapper.toEntity(userDTO);
        userEntity.setId(idGenerator.getAndIncrement());
        userEntity.setPassword("default123"); // 设置默认密码
        userRepository.put(userEntity.getId(), userEntity);
        return userMapper.toDTO(userEntity);
    }

    // 更新用户
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        UserEntity existingUser = userRepository.get(id);
        if (existingUser != null) {
            UserEntity updatedUser = userMapper.toEntity(userDTO);
            updatedUser.setId(id);
            updatedUser.setPassword(existingUser.getPassword()); // 保留原密码
            userRepository.put(id, updatedUser);
            return userMapper.toDTO(updatedUser);
        }
        return null;
    }

    // 删除用户
    public boolean deleteUser(Long id) {
        return userRepository.remove(id) != null;
    }
}