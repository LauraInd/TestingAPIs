package com.svalero.apievents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.UserController;
import com.svalero.apievents.domain.User;
import com.svalero.apievents.exception.UserNotFoundException;
import com.svalero.apievents.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("johndoe@example.com");
        user1.setPassword("securepassword123");
        user1.setActive(true);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("janedoe@example.com");
        user2.setPassword("anotherpassword456");
        user2.setActive(false);
    }

    // 📌 Test para obtener todos los usuarios
    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(userService, times(1)).getAllUsers();
    }

    // 📌 Test para agregar un nuevo usuario
    @Test
    void testAddUser() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(user1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    // 📌 Test para buscar un usuario por email
    @Test
    void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("johndoe@example.com")).thenReturn(user1);

        mockMvc.perform(get("/users/email")
                        .param("email", "johndoe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).getUserByEmail("johndoe@example.com");
    }

    // 📌 Test para obtener usuarios activos
    @Test
    void testGetActiveUsers() throws Exception {
        List<User> activeUsers = Arrays.asList(user1);
        when(userService.getActiveUsers()).thenReturn(activeUsers);

        mockMvc.perform(get("/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(userService, times(1)).getActiveUsers();
    }

    // 📌 Test para actualizar un usuario (completo)
    @Test
    void testUpdateUserSuccess() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(user1);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    // 📌 Test para actualizar un usuario no encontrado
    @Test
    void testUpdateUserNotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    // 📌 Test para actualizar parcialmente un usuario
    @Test
    void testUpdateUserPartial() throws Exception {
        when(userService.updateUserPartial(anyLong(), any(Map.class))).thenReturn(user1);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Updated Name"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).updateUserPartial(anyLong(), any(Map.class));
    }

    // 📌 Test para eliminar un usuario
    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    // 📌 Test para eliminar un usuario que no existe
    @Test
    void testDeleteUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(anyLong());
    }
}
