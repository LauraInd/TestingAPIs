package com.svalero.apievents.repository;

import com.svalero.apievents.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository  extends CrudRepository<User, Long> {

    // Método para obtener todos los usuarios
    List<User> findAll();

    // Método para buscar un usuario por su email
    User findByEmail(String email);

    // Método para buscar todos los usuarios activos
    List<User> findByActiveTrue();
}