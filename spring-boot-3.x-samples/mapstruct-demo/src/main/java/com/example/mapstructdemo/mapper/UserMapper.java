package com.example.mapstructdemo.mapper;

import com.example.mapstructdemo.dto.UserDTO;
import com.example.mapstructdemo.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // 基本映射，字段名不同的需要指定
    @Mappings({
            @Mapping(source = "phone", target = "phoneNumber"),
            @Mapping(source = "birthDate", target = "age", qualifiedByName = "calculateAge"),
            @Mapping(source = "active", target = "status", qualifiedByName = "mapStatus")
    })
    UserDTO toDTO(UserEntity userEntity);

    // 反向映射，忽略password字段
    @Mappings({
            @Mapping(source = "phoneNumber", target = "phone"),
            @Mapping(source = "status", target = "active", qualifiedByName = "mapActiveStatus"),
            @Mapping(target = "password", ignore = true)
    })
    UserEntity toEntity(UserDTO userDTO);

    // 列表映射
    List<UserDTO> toDTOList(List<UserEntity> userEntities);
    List<UserEntity> toEntityList(List<UserDTO> userDTOs);

    // 自定义转换方法 - 计算年龄
    @Named("calculateAge")
    default Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // 自定义转换方法 - 将布尔状态映射为字符串
    @Named("mapStatus")
    default String mapStatus(boolean active) {
        return active ? "激活" : "禁用";
    }

    // 自定义转换方法 - 将字符串状态映射为布尔值
    @Named("mapActiveStatus")
    default boolean mapActiveStatus(String status) {
        return "激活".equals(status);
    }
}