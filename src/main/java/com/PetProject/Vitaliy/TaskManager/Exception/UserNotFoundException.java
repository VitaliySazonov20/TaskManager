package com.PetProject.Vitaliy.TaskManager.Exception;

import java.math.BigInteger;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(BigInteger id) {
        super("User with ID: " + id + " does not exist");
    }
    public UserNotFoundException(String email){
        super("User with email: " + email + " does not exist");
    }
}
