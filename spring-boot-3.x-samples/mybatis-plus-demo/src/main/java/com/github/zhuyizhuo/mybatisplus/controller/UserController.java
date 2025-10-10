package com.github.zhuyizhuo.mybatisplus.controller;

import com.github.zhuyizhuo.mybatisplus.model.User;
import com.github.zhuyizhuo.mybatisplus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户管理相关API")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取所有用户", description = "返回系统中所有用户的列表")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.list();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "获取用户及其角色信息", description = "返回系统中所有用户及其角色信息的列表")
    @GetMapping("/with-roles")
    public ResponseEntity<List<User>> getAllUsersWithRoles() {
        List<User> users = userService.getAllUsersWithRoles();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "根据ID获取用户", description = "根据用户ID返回特定用户信息")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "根据ID获取用户及其角色信息", description = "根据用户ID返回特定用户及其角色信息")
    @GetMapping("/{id}/with-roles")
    public ResponseEntity<User> getUserWithRolesById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            // 获取并设置用户的角色信息
            List<User> usersWithRoles = userService.getAllUsersWithRoles();
            for (User u : usersWithRoles) {
                if (u.getId().equals(id)) {
                    user.setRoles(u.getRoles());
                    break;
                }
            }
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "根据角色ID获取用户", description = "根据角色ID返回拥有该角色的用户列表")
    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<User>> getUsersByRoleId(@PathVariable Long roleId) {
        List<User> users = userService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "分页获取用户", description = "分页返回系统中的用户列表")
    @GetMapping("/page")
    public ResponseEntity<List<User>> getUsersByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<User> users = userService.getUsersByPage(page, pageSize);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "创建新用户", description = "创建一个新的用户")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        boolean saved = userService.save(user);
        if (saved) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "创建用户并分配角色", description = "创建一个新用户并分配角色")
    @PostMapping("/with-roles")
    public ResponseEntity<User> createUserWithRoles(
            @RequestBody User user,
            @RequestParam List<Long> roleIds) {
        boolean saved = userService.saveUserWithRoles(user, roleIds);
        if (saved) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "更新用户信息", description = "更新指定ID的用户信息")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {
        userDetails.setId(id);
        boolean updated = userService.updateById(userDetails);
        if (updated) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "更新用户角色", description = "更新指定用户的角色信息")
    @PutMapping("/{id}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @RequestParam List<Long> roleIds) {
        boolean updated = userService.updateUserRoles(id, roleIds);
        if (updated) {
            return ResponseEntity.ok("用户角色更新成功");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "删除用户", description = "删除指定ID的用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.removeById(id);
        if (deleted) {
            return ResponseEntity.ok("用户删除成功");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}