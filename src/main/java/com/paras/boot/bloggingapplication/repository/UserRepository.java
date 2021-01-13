package com.paras.boot.bloggingapplication.repository;



import java.util.Optional;

import com.paras.boot.bloggingapplication.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 1460344
 */
public interface UserRepository extends JpaRepository<Users,Integer> {
    Optional<Users> findByEmail(String username);

}
