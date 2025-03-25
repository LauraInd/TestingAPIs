package com.svalero.apievents.service;

import com.svalero.apievents.domain.User;
import com.svalero.apievents.exception.UserNotFoundException;
import com.svalero.apievents.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Buscar un usuario por email
    //public User getUserByEmail(String email) {return userRepository.findByEmail(email);}
    public User getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    // Obtener usuarios activos
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    // Guardar un nuevo usuario
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Actualizar un usuario por ID
    public User updateUser(Long id, User userDetails) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Actualizar los campos necesarios
        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        return userRepository.save(existingUser);
    }

    public User updateUserPartial(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
            }
        });

        return userRepository.save(user);
    }


    // Eliminar un usuario por ID
    public void deleteUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}

