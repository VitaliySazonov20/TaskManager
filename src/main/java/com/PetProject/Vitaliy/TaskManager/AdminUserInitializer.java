package com.PetProject.Vitaliy.TaskManager;


import com.PetProject.Vitaliy.TaskManager.Repository.UserCredentialsRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserRepository;
import com.PetProject.Vitaliy.TaskManager.Service.UserCredentialsService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class AdminUserInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdminUser(){
        if(userCredentialsService.getCredentialsByRole(Role.ADMIN).isEmpty()){
            User admin = new User();
            //set name and email for user
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("FakeAdminEmail@gmail.com");


            UserCredentials adminCreds = new UserCredentials();

            adminCreds.setUser(admin);
            //encrypt and save password
            adminCreds.setPasswordHash(passwordEncoder.encode("password"));
            adminCreds.setRole(Role.ADMIN);
            admin.setUserCredentials(adminCreds);
            userCredentialsService.saveUserCred(adminCreds);
            userService.saveUser(admin);
        }
    }
}
