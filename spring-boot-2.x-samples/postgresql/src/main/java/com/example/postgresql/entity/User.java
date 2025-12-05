package com.example.postgresql.entity;

import javax.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "users", schema = "demo")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String phone;
}