package com.PetProject.Vitaliy.TaskManager.Model;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class RegistrationForm {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Please create a password")
    @Size(min = 6, message = "Password must contain atleast 6 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    @Size(min = 6)
    private String confirmationPassword;

    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordsMatch(){
        return password != null && password.equals(confirmationPassword);
    }


    public RegistrationForm(String firstName, String lastName, String email, String password, String confirmationPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmationPassword = confirmationPassword;

    }

    public RegistrationForm() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmationPassword() {
        return confirmationPassword;
    }

    public void setConfirmationPassword(String confirmationPassword) {
        this.confirmationPassword = confirmationPassword;
    }

    @Override
    public String toString() {
        return "RegistrationForm{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
