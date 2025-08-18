package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.DatabaseDataInitializer;
import com.PetProject.Vitaliy.TaskManager.DatabaseSchemaInitializer;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseResetService {

    @Autowired
    private DatabaseSchemaInitializer databaseSchemaInitializer;

    @Autowired
    private DatabaseDataInitializer databaseDataInitializer;

    @Autowired
    private EntityManager entityManager;



    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetDatabase(){

        entityManager.createNativeQuery("DELETE FROM task_comments").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM tasks").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM user_credentials").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM audit_log").executeUpdate();

//        databaseSchemaInitializer.initializeSchema();
//
//        databaseDataInitializer.initTestData();

//        entityManager.clear();

    }


}
