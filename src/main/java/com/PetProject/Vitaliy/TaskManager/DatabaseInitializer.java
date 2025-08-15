package com.PetProject.Vitaliy.TaskManager;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private DatabaseSchemaInitializer databaseSchemaInitializer;

    @Autowired
    private DatabaseDataInitializer databaseDataInitializer;

    @PostConstruct
    public void initialize(){
        databaseSchemaInitializer.initializeSchema();

        databaseDataInitializer.initTestData();
    }
}
