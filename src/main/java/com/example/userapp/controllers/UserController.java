package com.example.userapp.controllers;

import com.example.userapp.entities.UserEntity;
import com.example.userapp.services.UserServices;
import com.example.userapp.utilities.UserCsvRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping
    public List<UserEntity> getAllUsers(@RequestParam(required = false) String nome,
                                        @RequestParam(required = false) String cognome) {
        return userServices.getUserEntities(nome, cognome);
    }


    @PostMapping
    public UserEntity createUser(@RequestBody UserEntity userEntity) {
        return userServices.addUser(userEntity);
    }

    @PutMapping("/{id}")
    public UserEntity updateUser(@PathVariable Long id, @RequestBody UserEntity userDetails) {
        return userServices.update(id, userDetails);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userServices.delete(id);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<UserCsvRecord> csvToBean = new CsvToBeanBuilder<UserCsvRecord>(reader)
                    .withType(UserCsvRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<UserCsvRecord> records = csvToBean.parse();
            if (records.isEmpty()) {
                return ResponseEntity.badRequest().body("No user records found.");
            }

            List<UserEntity> saved = userServices.saveUserEntities(records);
            int skipped = records.size() - saved.size();

            String message = "Uploaded " + saved.size() + " users successfully.";
            if (skipped > 0) {
                message += " Skipped " + skipped + " duplicate email(s).";
            }

            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing CSV file.");
        }
    }


}