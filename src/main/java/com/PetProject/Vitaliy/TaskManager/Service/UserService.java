package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Repository.UserCredentialsRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserCredentialsRepository userCredRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Transactional
    public void saveUser(User user){
        userRepo.save(user);
    }

    public boolean checkIfEmailExists(String email){
        return userRepo.existsByEmail(email);
    }

    @Transactional
    public void registerUser(RegistrationForm form){
        User user = new User(form.getFirstName(), form.getLastName(), form.getEmail());
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setRole(Role.USER);
        userCredentials.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        userCredentials.setUser(user);
        user.setUserCredentials(userCredentials);
        userRepo.save(user);
        userCredRepo.save(userCredentials);
    }

    @Transactional
    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    @Transactional
    public User getUserById(BigInteger id){
        if(!userRepo.existsById(id)){
            throw new UserNotFoundException(id);
        }
        return userRepo.getById(id);
    }

    @Transactional
    public void deleteUserById(BigInteger id){
        if(!userRepo.existsById(id)){
            throw new UserNotFoundException(id);
        }
        userRepo.deleteById(id);
    }

    @Transactional
    public User getUserByEmail(String email){
        if(!userRepo.existsByEmail(email)){
            throw new UserNotFoundException(email);
        }
        return userRepo.getByEmail(email);
    }
}
