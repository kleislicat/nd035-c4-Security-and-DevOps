package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {

  private OrderController orderController;

  private UserRepository userRepository = mock(UserRepository.class);
  private OrderRepository orderRepository = mock(OrderRepository.class);
  private User user;

  @Before
  public void setUp() {
    orderController = new OrderController();
    TestUtils.injectObject(orderController, "userRepository", userRepository);
    TestUtils.injectObject(orderController, "orderRepository", orderRepository);

    user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    when(userRepository.findByUsername("user")).thenReturn(user);

    Item item = new Item();
    item.setId(1L);
    item.setName("Round Widget");
    item.setPrice(BigDecimal.valueOf(2.99));
    item.setDescription("A widget that is round");

    Cart cart = new Cart();
    cart.setId(1L);
    cart.addItem(item);
    cart.setUser(user);

    user.setCart(cart);
  }

  @Test
  public void submit_shouldReturnUserOrder() {
    final ResponseEntity<UserOrder> response = orderController.submit("user");
    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    assertEquals(user.getUsername(), response.getBody().getUser().getUsername());
  }

  @Test
  public void submit_shouldReturn_NOT_FOUND() {
    final ResponseEntity<UserOrder> response = orderController.submit("nonExistentUser");
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());

  }

  @Test
  public void getOrdersForUser_shouldReturnUserOrderList(){
    final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user");
    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
  }

  @Test
  public void getOrdersForUser_shouldReturn_NOT_FOUND() {
    final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonExistentUser");
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

}
