package com.paras.boot.bloggingapplication.controllers;

import com.paras.boot.bloggingapplication.dto.LoginDto;
import com.paras.boot.bloggingapplication.dto.RegisterUserDTO;
import com.paras.boot.bloggingapplication.models.Users;
import com.paras.boot.bloggingapplication.repository.UserRepository;
import com.paras.boot.bloggingapplication.util.EntityHawk;
import com.paras.boot.bloggingapplication.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.KeyAlreadyExistsException;
import javax.validation.Valid;
import java.util.Optional;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 1460344
 */
@RestController
public class UserController extends EntityHawk {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/login/")
    public ResponseEntity loginUser( @RequestBody LoginDto loginDto) {
    if(loginDto.getEmail()!=null && loginDto.getPassword()!=null)
    {
        Optional<Users> user=userRepository.findByEmail(loginDto.getEmail());
        if(user.isPresent() && user.get().getPassword().equals(loginDto.getPassword()))
        {
            JWTUtils utils=new JWTUtils();
            String token=utils.CreateJWTToken(user.get());
            return genericResponse(token);
        }
    }
         return genericResponse("Invalid Username or Password");
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO)
    {

        Optional<Users> checkUser=userRepository.findByEmail(registerUserDTO.getEmail());
        if(checkUser.isPresent())
        {
            throw new KeyAlreadyExistsException();
        }
        Users user=new Users();
        user.setUserName(registerUserDTO.getName());
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(registerUserDTO.getPassword());

        userRepository.save(user);
        return genericResponse("User Registered");
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException ex)
    {

        String key=null;
        String value=null;
        for(ObjectError error:ex.getBindingResult().getAllErrors())
        {
            key=((FieldError) error).getField();
            value = error.getDefaultMessage();
        }
        return genericResponse(key+" "+value);
    }
}
