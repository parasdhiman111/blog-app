package com.paras.boot.bloggingapplication.controllers;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.paras.boot.bloggingapplication.JWTFilter;
import com.paras.boot.bloggingapplication.dto.PostDTO;
import com.paras.boot.bloggingapplication.dto.UpdatePostDTO;
import com.paras.boot.bloggingapplication.models.Posts;
import com.paras.boot.bloggingapplication.models.Users;
import com.paras.boot.bloggingapplication.repository.PostRepository;
import com.paras.boot.bloggingapplication.repository.UserRepository;
import com.paras.boot.bloggingapplication.util.Constants;
import com.paras.boot.bloggingapplication.util.EntityHawk;
import com.paras.boot.bloggingapplication.util.PostMapper;
import io.jsonwebtoken.Jwts;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import io.jsonwebtoken.Claims;
import org.springframework.web.client.HttpServerErrorException;

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
@RequestMapping("/api")
public class GlobalController extends EntityHawk {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/getPostCount")
    public ResponseEntity getPostCount(@RequestHeader("authorization") String  token)
    {
       Claims claims=validateToken(token);
        List<Posts> posts=postRepository.findAll();
        return genericResponse(posts.size());

    }

    @PostMapping("/publish")
    public ResponseEntity createNewPost(@Valid @RequestBody PostDTO postDTO, @RequestHeader("authorization") String  token)
    {
        System.out.println("Authorization: "+token);
        Claims claims=validateToken(token);
        System.out.println("claims: "+claims.get("name")+" "+claims.get("email")+" "+claims.get("user_id"));
        Optional<Users> users= userRepository.findByEmail(claims.get("email").toString());
        if(users.isPresent())
        {
            Posts posts=new Posts();
            posts.setPostTitle(postDTO.getTitle());
            posts.setPostBody(postDTO.getBody());
            posts.setCreatedOn(new Date());
            posts.setUpdatedOn(new Date());
            posts.setPublishedBy(users.get());
            posts.setIsDeleted(true);

            postRepository.save(posts);
            return genericResponse("Published");
        }

        return genericResponse("details are not correct");
    }

    @GetMapping("/getPost")
    public ResponseEntity getPosts(@RequestHeader("authorization") String  token)
    {
        List<Posts> posts=postRepository.findAll();
        PostMapper mapper=new PostMapper();
        List<Map> map=new ArrayList<>();
        for(Posts posts1:posts)
        {
            map.add(mapper.postDetailsToMap(posts1));
        }
        return genericResponse(map);
    }

    @GetMapping("/getPost/{postId}")
    public ResponseEntity getPostById(@PathVariable String postId)
    {
        Optional<Posts> posts=postRepository.findById(Integer.parseInt(postId));
        if(posts.isPresent())
        {
            PostMapper mapper=new PostMapper();
            Map map=mapper.postDetailsToMap(posts.get());
            return  genericResponse(map);
        }
        return genericResponse("Post Not Found");

    }


    @GetMapping("/getPostByUser/{userId}")
    public ResponseEntity getPostByUserId(@PathVariable String userId)
    {
        List<Posts> posts=postRepository.findByPublishedByUserId(Integer.parseInt(userId));
        if(posts.size()>0)
        {
            PostMapper mapper=new PostMapper();
            List<Map> map=new ArrayList<>();
            for(Posts posts1:posts)
            {
                map.add(mapper.postDetailsToMap(posts1));
            }
            return genericResponse(map);
        }
        return genericResponse("No posts by user Id "+userId);
    }

    @PostMapping("/updatePost")
    public ResponseEntity updatePost(@Valid @RequestBody UpdatePostDTO updatePostDTO, @RequestHeader("authorization") String  token)
    {
        Optional<Posts> posts=postRepository.findById(updatePostDTO.getPost_id());
        if(posts.isPresent())
        {
            posts.get().setPostTitle(updatePostDTO.getTitle());
            posts.get().setPostBody(updatePostDTO.getBody());
            postRepository.save(posts.get());
            return genericResponse("Post updated");
        }
        return genericResponse("updatePost");
    }

    @GetMapping("/deletePost/{postId}")
    public ResponseEntity deletePost(@RequestHeader("authorization") String  token,@PathVariable String postId)
    {
        Optional<Posts> posts=postRepository.findById(Integer.parseInt(postId));
        if(posts.isPresent())
        {
            postRepository.delete(posts.get());
            return genericResponse("Post Deleted");
        }
        return genericResponse("Post Not Found");
    }

    private Claims validateToken(String token) {
        String jwtToken = token.replace("Bearer ", "");
        return Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(jwtToken).getBody();
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