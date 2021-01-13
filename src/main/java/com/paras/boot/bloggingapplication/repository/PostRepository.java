package com.paras.boot.bloggingapplication.repository;


import java.util.List;
import java.util.Optional;

import com.paras.boot.bloggingapplication.models.Posts;
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
public interface PostRepository extends JpaRepository<Posts,Integer> {

    List<Posts> findByPublishedByUserId(int id);

}
