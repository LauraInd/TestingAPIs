package com.svalero.apievents;

import com.svalero.apievents.domain.User;
import com.svalero.apievents.exception.UserNotFoundException;
import com.svalero.apievents.repository.UserRepository;
import com.svalero.apievents.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setActive(true);
    }

    // 📌 Test para obtener todos los usuarios
    @Test
    void testGetAllUsers() {
        List<User> userList = List.of(user);
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    // 📌 Test para obtener un usuario por email
    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("johndoe@example.com")).thenReturn(user);

        User result = userService.getUserByEmail("johndoe@example.com");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).findByEmail("johndoe@example.com");
    }

    // 📌 Test para obtener usuarios activos
    @Test
    void testGetActiveUsers() {
        when(userRepository.findByActiveTrue()).thenReturn(List.of(user));

        List<User> result = userService.getActiveUsers();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(userRepository, times(1)).findByActiveTrue();
    }

    // 📌 Test para guardar un usuario
    @Test
    void testSaveUser() {
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getName());
        verify(userRepository, times(1)).save(user);
    }

    // 📌 Test para actualizar un usuario (completo)
    @Test
    void testUpdateUserSuccess() throws UserNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User userDetails = new User();
        userDetails.setName("Updated Name");
        userDetails.setEmail("updated@example.com");

        User updatedUser = userService.updateUser(1L, userDetails);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    // 📌 Test para actualizar un usuario que no existe
    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User userDetails = new User();
        userDetails.setName("Updated Name");

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, userDetails));
        verify(userRepository, never()).save(any(User.class));
    }

    // 📌 Test para actualizar parcialmente un usuario
    @Test
    void testUpdateUserPartial() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Partial Update");

        User updatedUser = userService.updateUserPartial(1L, updates);

        assertEquals("Partial Update", updatedUser.getName());
        verify(userRepository, times(1)).save(user);
    }

    // 📌 Test para eliminar un usuario
    @Test
    void testDeleteUserSuccess() throws UserNotFoundException {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    // 📌 Test para eliminar un usuario que no existe
    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
