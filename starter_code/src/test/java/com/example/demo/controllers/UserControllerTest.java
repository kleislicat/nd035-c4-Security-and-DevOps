package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

  private UserController userController;
  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

  @Before
  public void setUp() {
    userController = new UserController();
    TestUtils.injectObject(userController, "userRepository", userRepository);
    TestUtils.injectObject(userController, "cartRepository", cartRepository);
    TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
  }

  @Test
  public void findById_shouldReturnUser() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
    final ResponseEntity<User> response = userController.findById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    assertEquals("user", response.getBody().getUsername());
  }

  @Test
  public void findByUserName_shouldReturnUser() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    when(userRepository.findByUsername("user")).thenReturn(user);
    final ResponseEntity<User> response = userController.findByUserName("user");

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    assertEquals("user", response.getBody().getUsername());
  }

  @Test
  public void createUser_shouldReturnUser() {
    when(bCryptPasswordEncoder.encode("password")).thenReturn("thisIsHashed");
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("user");
    userRequest.setPassword("password");
    userRequest.setConfirmPassword("password");
    final ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    User u = response.getBody();
    assertNotNull(u);
    assertEquals(0, u.getId());
    assertEquals("user",u.getUsername());
    assertEquals("thisIsHashed", u.getPassword());
  }

  @Test
  public void createUser_shouldReturn_BAD_REQUEST() {
    when(bCryptPasswordEncoder.encode("pass")).thenReturn("thisIsHashed");
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("user");
    userRequest.setPassword("pass");
    userRequest.setConfirmPassword("pass");
    final ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }





}
