# MapStruct Demo

这是一个演示 MapStruct 在 Spring Boot 3.x 环境中使用的示例项目。MapStruct 是一个用于 Java Bean 映射的代码生成器，它通过注解处理器在编译时生成高效的映射代码，避免了运行时反射带来的性能开销。

## 项目功能

本项目演示了 MapStruct 的核心功能：

- **基本映射**：实现实体类与DTO之间的自动转换
- **名称映射**：处理字段名称不一致的情况
- **自定义转换**：实现特殊类型的转换逻辑
- **集合映射**：List、Set 等集合类型的批量转换
- **字段忽略**：灵活控制哪些字段需要映射
- **RESTful API**：提供接口测试映射功能
- **Web演示界面**：直观展示映射效果

## 技术栈

- Spring Boot 3.2.5
- MapStruct 1.5.5.Final
- Lombok 1.18.30
- Thymeleaf（用于页面渲染）
- Java 17+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/zhuyizhuo/spring-boot-samples.git
cd spring-boot-samples/spring-boot-3.x-samples/mapstruct-demo
```

### 2. 构建项目

```bash
mvn clean package
```

### 3. 运行应用

```bash
java -jar target/mapstruct-demo-1.0-SNAPSHOT.jar
```

应用启动后会显示以下访问地址：
- 首页：http://localhost:8080
- 演示页面：http://localhost:8080/test
- API接口：http://localhost:8080/api/users

## 项目结构

```
src/
├── main/
│   ├── java/com/example/mapstructdemo/
│   │   ├── config/             # 配置类
│   │   │   └── StartupListener.java  # 启动监听器
│   │   ├── controller/         # 控制器
│   │   │   └── UserController.java   # 用户相关接口
│   │   ├── dto/                # 数据传输对象
│   │   │   └── UserDTO.java
│   │   ├── entity/             # 实体类
│   │   │   └── UserEntity.java
│   │   ├── mapper/             # MapStruct 映射接口
│   │   │   └── UserMapper.java
│   │   ├── service/            # 业务逻辑
│   │   │   └── UserService.java
│   │   └── MapstructDemoApplication.java  # 应用入口
│   └── resources/
│       ├── templates/          # Thymeleaf 模板
│       │   ├── index.html      # 首页
│       │   └── test.html       # 演示页面
│       └── application.yml     # 应用配置
└── test/                       # 测试代码
```

## MapStruct 使用示例

### 1. 定义映射接口

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    // 基本映射
    UserDTO entityToDto(UserEntity entity);
    
    // 反向映射
    UserEntity dtoToEntity(UserDTO dto);
    
    // 集合映射
    List<UserDTO> entityListToDtoList(List<UserEntity> entities);
    
    // 自定义转换
    @Mapping(target = "fullName", expression = "java(entity.getFirstName() + \" \" + entity.getLastName())")
    UserDTO entityToDtoWithCustomMapping(UserEntity entity);
}
```

### 2. 更多高级用法

#### 2.1 使用表达式映射

```java
@Mapper(componentModel = "spring")
public interface AdvancedMapper {
    // 使用Java表达式进行复杂映射
    @Mapping(target = "fullName", expression = "java(source.getFirstName() + \" \" + source.getLastName())")
    @Mapping(target = "greeting", expression = "java(\"Hello, \" + source.getFirstName() + \"!\")")
    TargetDTO sourceToTarget(SourceEntity source);
}
```

#### 2.2 日期和时间格式化

```java
@Mapper(componentModel = "spring", imports = {java.time.format.DateTimeFormatter.class})
public interface DateMapper {
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Mapping(target = "birthDateStr", expression = "java(source.getBirthDate() != null ? source.getBirthDate().format(DATE_FORMATTER) : null)")
    UserDTO entityToDto(UserEntity source);
    
    // 也可以使用格式化注解
    @Mapping(target = "birthDate", dateFormat = "yyyy-MM-dd")
    UserEntity dtoToEntity(UserDTO dto);
}
```

#### 2.3 映射继承

```java
@Mapper(componentModel = "spring")
public interface InheritMapper {
    // 基础映射
    @Mapping(source = "id", target = "userId")
    BaseDTO sourceToBase(SourceEntity source);
    
    // 继承基础映射并添加额外映射
    @InheritConfiguration(name = "sourceToBase")
    @Mapping(source = "email", target = "userEmail")
    ExtendedDTO sourceToExtended(SourceEntity source);
    
    // 反向继承配置
    @InheritInverseConfiguration(name = "sourceToBase")
    SourceEntity baseToSource(BaseDTO dto);
}
```

#### 2.4 条件映射

```java
@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ConditionalMapper {
    // 忽略null值映射
    @Mapping(target = "email", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserDTO dto, @MappingTarget UserEntity entity);
    
    // 使用自定义条件方法
    @Mapping(target = "status", condition = "java(source.isActive() && source.getLastLogin() != null)")
    TargetDTO withConditionMapping(SourceEntity source);
}
```

#### 2.5 默认值和常量

```java
@Mapper(componentModel = "spring")
public interface DefaultValueMapper {
    // 设置默认值
    @Mapping(target = "status", defaultValue = "PENDING")
    @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdBy", constant = "system")
    UserDTO entityToDto(UserEntity source);
}
```

#### 2.6 映射依赖注入

```java
@Mapper(componentModel = "spring", uses = {AddressMapper.class, PhoneMapper.class})
public interface UserWithDependenciesMapper {
    // MapStruct会自动注入并使用AddressMapper和PhoneMapper
    @Mapping(source = "address", target = "addressDTO")
    @Mapping(source = "phones", target = "phoneDTOs")
    UserDTO entityToDto(UserEntity source);
}

// 依赖的映射器
@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDTO toDTO(AddressEntity entity);
}
```

#### 2.7 映射列表中的嵌套对象

```java
@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface OrderMapper {
    // 自动映射订单中的订单项列表
    @Mapping(source = "orderItems", target = "orderItemDTOs")
    OrderDTO toDTO(OrderEntity entity);
    
    List<OrderDTO> toDTOList(List<OrderEntity> entities);
}

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "productName", target = "itemName")
    OrderItemDTO toDTO(OrderItemEntity entity);
}
```

#### 2.8 使用构建器模式

```java
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface BuilderMapper {
    // MapStruct会使用构建器创建不可变对象
    TargetImmutableDTO sourceToTarget(SourceEntity source);
}

// 不可变DTO示例
public class TargetImmutableDTO {
    private final String name;
    private final int age;
    
    // 私有构造函数
    private TargetImmutableDTO(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // 构建器静态方法
    public static Builder builder() {
        return new Builder();
    }
    
    // 构建器内部类
    public static class Builder {
        private String name;
        private int age;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public TargetImmutableDTO build() {
            return new TargetImmutableDTO(name, age);
        }
    }
    
    // getter方法
    public String getName() { return name; }
    public int getAge() { return age; }
}

### 2. 在服务中使用

```java
@Service
public class UserService {
    
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public UserDTO getUserById(Long id) {
        UserEntity entity = getUserFromDatabase(id); // 模拟从数据库获取
        return userMapper.entityToDto(entity);
    }
}
```

## API 接口

### 获取用户列表

- **URL**: `/api/users`
- **方法**: `GET`
- **响应**: 包含用户信息的JSON数组

### 获取单个用户

- **URL**: `/api/users/{id}`
- **方法**: `GET`
- **响应**: 单个用户的JSON对象

### 创建用户

- **URL**: `/api/users`
- **方法**: `POST`
- **请求体**: 用户DTO的JSON对象
- **响应**: 创建的用户信息

### 更新用户

- **URL**: `/api/users/{id}`
- **方法**: `PUT`
- **请求体**: 更新的用户DTO JSON对象
- **响应**: 更新后的用户信息

### 删除用户

- **URL**: `/api/users/{id}`
- **方法**: `DELETE`
- **响应**: 删除成功状态

## 页面功能

1. **首页**：介绍项目功能和MapStruct的特点
2. **演示页面**：展示用户数据的映射效果，直观对比原数据和映射后的数据

## MapStruct 优势

- **性能高效**：编译时生成代码，无反射开销
- **类型安全**：编译时检查类型正确性
- **易于维护**：声明式配置，代码简洁
- **智能映射**：同名同类型字段自动映射
- **灵活配置**：支持自定义转换逻辑

## 参考文档

- [MapStruct 官方文档](https://mapstruct.org/documentation/stable/reference/html/)
- [Spring Boot 官方文档](https://spring.io/guides/gs/spring-boot/)

## 许可证

本项目采用 MIT 许可证 - 详情请查看 [LICENSE](https://github.com/zhuyizhuo/spring-boot-samples/blob/main/LICENSE) 文件