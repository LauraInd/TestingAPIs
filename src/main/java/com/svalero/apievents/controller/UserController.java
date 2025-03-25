package com.svalero.apievents.controller;

import com.svalero.apievents.domain.User;
import com.svalero.apievents.exception.UserNotFoundException;
import com.svalero.apievents.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("BEGIN getAllUsers");
        List<User> users = userService.getAllUsers();
        logger.info("END getAllUsers - Total users fetched: {}", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Agregar un nuevo usuario
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        logger.info("BEGIN addUser - Adding user: {}", user.getEmail());
        User newUser = userService.saveUser(user);
        logger.info("END addUser - User added with ID: {}", newUser.getId());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // Buscar un usuario por email
    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) throws UserNotFoundException {
        logger.info("BEGIN getUserByEmail - Searching user with email: {}", email);
        User user = userService.getUserByEmail(email);
        logger.info("END getUserByEmail - User found: {}", user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Obtener usuarios activos
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        logger.info("BEGIN getActiveUsers - Fetching active users");
        List<User> activeUsers = userService.getActiveUsers();
        logger.info("END getActiveUsers - Total active users fetched: {}", activeUsers.size());
        return new ResponseEntity<>(activeUsers, HttpStatus.OK);
    }

    // Actualizar un usuario por ID
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        logger.info("BEGIN updateUser - Updating user with ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            logger.info("END updateUser - User updated with ID: {}", id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Error in updateUser - User not found with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Actualizar parcialmente un usuario por ID
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("BEGIN updateUserPartial - Partially updating user with ID: {}", id);
        User updatedUser = userService.updateUserPartial(id, updates);
        logger.info("END updateUserPartial - User updated with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("BEGIN deleteUser - Deleting user with ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("END deleteUser - User deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            logger.error("Error in deleteUser - User not found with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Manejar excepciones de usuario no encontrado
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {
        logger.error("Handling UserNotFoundException - {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
