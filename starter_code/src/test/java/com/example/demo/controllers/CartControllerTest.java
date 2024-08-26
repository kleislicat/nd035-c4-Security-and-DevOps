package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CartControllerTest {

  private CartController cartController;
  private ModifyCartRequest modifyCartRequest;
  private User user;
  private Item item;

  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private ItemRepository itemRepository = mock(ItemRepository.class);

  @Before
  public void setUp() {
    cartController = new CartController();
    TestUtils.injectObject(cartController, "userRepository", userRepository);
    TestUtils.injectObject(cartController, "cartRepository", cartRepository);
    TestUtils.injectObject(cartController, "itemRepository", itemRepository);

    this.modifyCartRequest = new ModifyCartRequest();
    this.modifyCartRequest.setItemId(1L);
    this.modifyCartRequest.setUsername("user");
    this.modifyCartRequest.setItemId(1L);
    this.modifyCartRequest.setQuantity(5);

    user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    when(userRepository.findByUsername("user")).thenReturn(user);

    item = new Item();
    item.setId(1L);
    item.setName("Round Widget");
    item.setPrice(BigDecimal.valueOf(2.99));
    item.setDescription("A widget that is round");
    when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));

    Cart cart = new Cart();
    cart.setId(1L);
    cart.addItem(item);
    cart.setUser(user);

    user.setCart(cart);


  }

  @Test
  public void addTocart_shouldReturnCart() {
    final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
  }

  @Test
  public void addTocart_shouldReturn_NOT_FOUND_NullUser() {
    modifyCartRequest.setUsername(null);
    when(userRepository.findByUsername("user")).thenReturn(null);

    final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

  @Test
  public void addTocart_shouldReturn_NOT_FOUND_EmptyCart() {
    when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

    final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

  @Test
  public void removeFromcart_shouldReturnCart() {
    final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
  }

  @Test
  public void removeFromcart_shouldReturn_NOT_FOUND_NullUser() {
    modifyCartRequest.setUsername(null);
    when(userRepository.findByUsername("user")).thenReturn(null);

    final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

  @Test
  public void removeFromcart_shouldReturn_NOT_FOUND_ItemNotPresent() {
    modifyCartRequest.setItemId(67L);

    final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

}
