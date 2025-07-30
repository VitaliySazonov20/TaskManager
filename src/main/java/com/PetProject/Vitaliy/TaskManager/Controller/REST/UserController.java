package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "User management")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of all users")
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUserModels(){
        try{
            List<User> users = userService.getAllUsers();
            if(users.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            List<UserModel> userModels = users.stream()
                    .filter(Objects::nonNull)
                    .map(user->{
                        return new UserModel(
                               user.getId(),
                               user.getFirstName(),
                               user.getLastName(),
                               user.getEmail(),
                               user.getUserCredentials().getRole()
                       );
                    }).collect(Collectors.toList());
            return ResponseEntity.ok(userModels);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User has been deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id){
        if(id == null|| id.compareTo(BigInteger.ZERO)<=0){
            return ResponseEntity.badRequest().build();
        }
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("User has been deleted");
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }
    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id){

        if(id == null || id.compareTo(BigInteger.ZERO)<=0){
            return ResponseEntity.badRequest()
                    .body("Id must be a positive number");
        }
        try{
            User user = userService.getUserById(id);
            UserModel userModel = new UserModel(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getUserCredentials().getRole());
            return ResponseEntity.ok(userModel);
        } catch (UserNotFoundException e){
            return  ResponseEntity.notFound().build();
        }catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Database error occurred");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Update User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id,
            @Valid @RequestBody UserModel userModel){
        if (id == null || id.compareTo(BigInteger.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body("ID must be a positive number");
        }

        try {
            User user = userService.getUserById(id);
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());

            if(userService.checkIfEmailExists(userModel.getEmail())){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email already exists");
            }

            user.setEmail(user.getEmail());
            userService.saveUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("Failed to update user");
        }

    }

}
