package com.PetProject.Vitaliy.TaskManager;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Scheduled(cron = "0 1 0 * * *")
    public void afterDatabaseResetReInit() {
        databaseSchemaInitializer.initializeSchema();
        databaseDataInitializer.initTestData();
    }

}
