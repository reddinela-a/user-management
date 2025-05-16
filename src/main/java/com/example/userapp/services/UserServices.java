package com.example.userapp.services;

import com.example.userapp.entities.UserEntity;
import com.example.userapp.repositories.UserRepository;
import com.example.userapp.utilities.UserCsvRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    public UserEntity addUser(UserEntity userEntity) {
        String mail = userEntity.getMail();
        if (mail == null || mail.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (userRepository.existsByMail(mail)) {
            throw new IllegalStateException("Email already exists: " + mail);
        }
        return userRepository.save(userEntity);
    }

    public List<UserEntity> getUserEntities(String nome, String cognome) {
        if (nome != null || cognome != null) {
            return userRepository.searchByNomeAndCognome(nome, cognome);
        }
        return userRepository.findAll();
    }

    public UserEntity update(Long id, UserEntity userDetails) {
        UserEntity user = userRepository.findById(id).orElseThrow();

        Optional.ofNullable(userDetails.getNome()).ifPresent(user::setNome);
        Optional.ofNullable(userDetails.getCognome()).ifPresent(user::setCognome);
        Optional.ofNullable(userDetails.getMail()).ifPresent(user::setMail);
        Optional.ofNullable(userDetails.getIndirizzo()).ifPresent(user::setIndirizzo);

        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserEntity> saveUserEntities(List<UserCsvRecord> records) {
        List<UserEntity> savedUsers = new ArrayList<>();

        for (UserCsvRecord record : records) {
            String email = record.getMail();
            if (email != null && userRepository.existsByMail(email.trim())) {
                continue; // Skip this user, do not throw an exception
            }

            UserEntity user = new UserEntity();
            user.setNome(record.getNome());
            user.setCognome(record.getCognome());
            user.setMail(email);
            user.setIndirizzo(record.getIndirizzo());

            savedUsers.add(userRepository.save(user));
        }

        return savedUsers;
    }
}
