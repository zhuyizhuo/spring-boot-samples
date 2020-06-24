package com.github.zhuyizhuo.ldap.entity;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;

@Data
@Entry(base = "ou=people,dc=yizhuo,dc=github,dc=com", objectClasses = "inetOrgPerson")
public class Person implements Serializable {
    private static final long serialVersionUID = -7946768337975852352L;

    @Id
    private Name id;
    @DnAttribute(value = "uid", index = 3)
    private String uid;
    @Attribute(name = "cn")
    private String commonName;
    @Attribute(name = "sn")
    private String suerName;
    private String userPassword;

}
