package com.example.userapp.utilities;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class UserCsvRecord {

    @CsvBindByName(column = "Nome")
    private String nome;

    @CsvBindByName(column = "Cognome")
    private String cognome;

    @CsvBindByName(column = "Mail")
    private String mail;

    @CsvBindByName(column = "Indirizzo")
    private String indirizzo;

}