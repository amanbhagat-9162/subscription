package org.example.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.subscription.entity.User;
import org.example.subscription.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Aman");
        user.setEmail("aman@test.com");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aman"))
                .andExpect(jsonPath("$.email").value("aman@test.com"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsers_success() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Aman");

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aman"));
    }
}
