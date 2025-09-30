package com.github.zhuyizhuo.helloworld.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hello"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello, Spring Boot 3.x!"));
    }

    @Test
    void testGreetingWithName() throws Exception {
        String name = "TestUser";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/greeting")
                .param("name", name))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.format("Hello, %s! Welcome to Spring Boot 3.x!", name)));
    }

    @Test
    void testGreetingWithoutName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/greeting"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello, Guest! Welcome to Spring Boot 3.x!"));
    }

}