package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.util.Mapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

  Logger logger = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OrderRepository orderRepository;


  @PostMapping("/submit/{username}")
  public ResponseEntity<UserOrder> submit(@PathVariable String username) {
    logger.info("OrderController.submit(): request by {}.", username);
    User user = userRepository.findByUsername(username);
    if (user == null) {
      logger.error("OrderController.submit(): Exception: user {} doesn't exist.", username);
      return ResponseEntity.notFound().build();
    }
    UserOrder order = UserOrder.createFromCart(user.getCart());
    orderRepository.save(order);
    logger.info("OrderController.submit(): New order {} was created successfully. ", Mapper.mapToJsonString(order));
    return ResponseEntity.ok(order);
  }

  @GetMapping("/history/{username}")
  public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
    logger.info("OrderController.getOrdersForUser(): request by {}.", username);
    User user = userRepository.findByUsername(username);
    if (user == null) {
      logger.error("OrderController.getOrdersForUser(): Exception: user {} doesn't exist.", username);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(orderRepository.findByUser(user));
  }
}
