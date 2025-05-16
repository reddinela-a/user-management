package com.example.userapp.repositories;

import com.example.userapp.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByNomeContainingAndCognomeContaining(String nome, String cognome);

    boolean existsByMail(String mail);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:cognome IS NULL OR LOWER(u.cognome) LIKE LOWER(CONCAT('%', :cognome, '%')))")
    List<UserEntity> searchByNomeAndCognome(@Param("nome") String nome, @Param("cognome") String cognome);
}