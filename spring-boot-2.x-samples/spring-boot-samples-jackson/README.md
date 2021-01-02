## Spring-boot Jackson 应用 Demo 

### 使用 Jackson 和 requestWrapper 对出入参进行统一加解密
Demo : UserController

### Jackson 全局配置与局部配置
局部配置优先级高于全局配置 
全局配置过滤出参 null 值及空串
全局配置日期序列化

### Jackson 的注解
注解所在的包 com.fasterxml.jackson.annotation
@JsonInclude
可以控制在哪些情况下才将被注解的属性转换成 json，例如只有属性不为 null 时。
```
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerInfoEntity {
    private String id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String height;
}
```
@JsonProperty 反序列化指定属性别名
@JsonIgnore 序列化、反序列化忽略属性
@JsonIgnoreProperties(value = {"aaa","bbb"})  
    @JsonIgnoreProperties 和 @JsonIgnore 的作用相同，都是告诉 Jackson 该忽略哪些属性，
    不同之处是 @JsonIgnoreProperties 是类级别的，并且可以同时指定多个属性。                                                               
@JsonIgnoreType 标注在类上，当其他类有该类作为属性时，该属性将被忽略。
```
@JsonIgnoreType
public class Para {
}

public class Resp {
    private String name;
    private Para para;
}
```
@JsonSetter 标注于 setter 方法上，类似 @JsonProperty ，也可以解决 json 键名称和 java pojo 字段名称不匹配的问题。
```
public class SomeEntity {
    private String desc;
    @JsonSetter("description")
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
```
@JsonAlias 为反序列化期间要接受的属性定义一个或多个替代名称，可以与@JsonProperty一起使用
```
public class UserVoByJson {
    @JsonAlias({"pass_word", "passWord"})
    @JsonProperty("pwd")
    private String password;
}
```
@JsonFormat 序列化、反序列化时，格式化时间

## 项目对应博客：

- http://zhuyizhuo.online/2020/07/12/spring-boot-jackson/

