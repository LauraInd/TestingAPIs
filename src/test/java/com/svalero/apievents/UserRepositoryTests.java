package com.svalero.apievents;

import com.svalero.apievents.domain.User;
import com.svalero.apievents.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest  // Anotación para pruebas de JPA con H2
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Crear usuarios de prueba
        user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("johndoe@example.com");
        user1.setPassword("securepassword123");
        user1.setActive(true);

        user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("janedoe@example.com");
        user2.setPassword("anotherpassword456");
        user2.setActive(false);

        // Guardar en la BD de prueba
        userRepository.save(user1);
        userRepository.save(user2);
    }

    // 📌 Test para obtener todos los usuarios
    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());  // Deben existir 2 usuarios
    }

    // 📌 Test para encontrar un usuario por email
    @Test
    void testFindByEmail() {
        User user = userRepository.findByEmail("johndoe@example.com");
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
    }

    // 📌 Test para obtener solo los usuarios activos
    @Test
    void testFindByActiveTrue() {
        List<User> activeUsers = userRepository.findByActiveTrue();
        assertEquals(1, activeUsers.size());  // Solo hay 1 usuario activo
        assertTrue(activeUsers.get(0).isActive());
    }
}
