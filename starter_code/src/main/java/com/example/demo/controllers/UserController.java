package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.util.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @GetMapping("/id/{id}")
  public ResponseEntity<User> findById(@PathVariable Long id) {
    return ResponseEntity.of(userRepository.findById(id));
  }

  @GetMapping("/{username}")
  public ResponseEntity<User> findByUserName(@PathVariable String username) {
    User user = userRepository.findByUsername(username);
    return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
  }

  @PostMapping("/create")
  public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
    logger.info("UserController.createUser(): createUserRequest {}.",
        Mapper.mapToJsonString(createUserRequest));
    User user = new User();
    user.setUsername(createUserRequest.getUsername());
    Cart cart = new Cart();
    cartRepository.save(cart);

    user.setCart(cart);
    if (createUserRequest.getPassword().length() < 7 ||
        !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
      logger.error(
          "UserController.createUser(): Exception: User '{}' was not created, because password is less than 7 characters or password does not match.",
          createUserRequest.getUsername());
      return ResponseEntity.badRequest().build();
    }
    user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
    userRepository.save(user);
    logger.info("UserController.createUser(): New user '{}' created successfully.",
        createUserRequest.getUsername());
    return ResponseEntity.ok(user);
  }

}
