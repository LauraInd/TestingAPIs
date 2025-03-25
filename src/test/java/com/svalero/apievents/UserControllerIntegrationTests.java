package com.svalero.apievents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser;
    private User invalidUser;

    private Long createdUserId;

    @BeforeEach
    void setUp() throws Exception {

        mockMvc.perform(delete("/users/1"));
        validUser = new User();
        validUser.setName("Laura");
        validUser.setEmail("laura" + UUID.randomUUID() + "@email.com");
        validUser.setPassword("securePass123");
        validUser.setCreationDate(LocalDate.now());
        validUser.setActive(true);

        invalidUser = new User();
        invalidUser.setName(null);
        invalidUser.setEmail("invalid");
        invalidUser.setPassword("123");

        // Crear usuario para operaciones con ID
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        User created = objectMapper.readValue(response, User.class);
        createdUserId = created.getId();
    }

    @Test
    void shouldReturn200_whenGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn201_whenCreateValidUser() throws Exception {
        validUser.setEmail("new" + System.currentTimeMillis() + "@email.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laura"));
    }

    @Test
    void shouldReturn400_whenCreateInvalidUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200_whenGetUserByEmail() throws Exception {
        mockMvc.perform(get("/users/email")
                        .param("email", validUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(validUser.getEmail()));
    }

    @Test
    void shouldReturn404_whenGetUserByNonExistentEmail() throws Exception {
        mockMvc.perform(get("/users/email")
                        .param("email", "noexist@email.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200_whenGetActiveUsers() throws Exception {
        mockMvc.perform(get("/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

   /* @Test
    void shouldReturn400_whenCreateUserWithDuplicateEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }*/

    @Test
    void shouldReturn404_whenUpdateNonExistentUser() throws Exception {
        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200_whenUpdateExistingUser() throws Exception {
        validUser.setName("Updated Name");
        mockMvc.perform(put("/users/" + createdUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldReturn200_whenPatchUser() throws Exception {
        String patchJson = "{\"active\": false}";
        mockMvc.perform(patch("/users/" + createdUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldReturn404_whenDeleteNonExistentUser() throws Exception {
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn204_whenDeleteExistingUser() throws Exception {
        mockMvc.perform(delete("/users/" + createdUserId))
                .andExpect(status().isNoContent());
    }
}

