package com.example.demo.controllers;

import com.example.demo.util.Mapper;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  Logger logger = LoggerFactory.getLogger(CartController.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private ItemRepository itemRepository;

  @PostMapping("/addToCart")
  public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
    logger.info("CartController.addTocart(): request {}.", Mapper.mapToJsonString(request));
    User user = userRepository.findByUsername(request.getUsername());
    if (user == null) {
      logger.error("CartController.addTocart(): Exception: user {} doesn't exist.",
          request.getUsername());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Optional<Item> item = itemRepository.findById(request.getItemId());
    if (!item.isPresent()) {
      logger.error("CartController.addTocart(): Exception: Item {} not found.",
          request.getItemId());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Cart cart = user.getCart();
    IntStream.range(0, request.getQuantity())
        .forEach(i -> cart.addItem(item.get()));
    cartRepository.save(cart);
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/removeFromCart")
  public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
    logger.info("CartController.removeFromcart(): ModifyCartRequest {}.",
        Mapper.mapToJsonString(request));
    User user = userRepository.findByUsername(request.getUsername());
    if (user == null) {
      logger.error("CartController.removeFromcart(): Exception: user '{}' doesn't exist. ",
          request.getUsername());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Optional<Item> item = itemRepository.findById(request.getItemId());
    if (!item.isPresent()) {
      logger.error("CartController.removeFromcart(): Exception: itemId '{}' not found.",
          request.getItemId());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Cart cart = user.getCart();
    logger.info("CartController.removeFromcart(): cart before remove {}.",
        Mapper.mapToJsonString(cart));
    IntStream.range(0, request.getQuantity())
        .forEach(i -> cart.removeItem(item.get()));
    logger.info("CartController.removeFromcart(): cart after remove {}.",
        Mapper.mapToJsonString(cart));
    cartRepository.save(cart);
    logger.info("CartController.removeFromcart(): itemId '{}' was successfully removed.",
        request.getItemId());
    return ResponseEntity.ok(cart);
  }

}
