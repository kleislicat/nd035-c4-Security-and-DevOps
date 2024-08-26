package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ItemControllerTest {

  private ItemController itemController;
  private ItemRepository itemRepository = mock(ItemRepository.class);

  private Item item;

  @Before
  public void setUp() {
    itemController = new ItemController();
    TestUtils.injectObject(itemController, "itemRepository", itemRepository);

    item = new Item();
    item.setId(1L);
    item.setName("Round Widget");
    item.setPrice(BigDecimal.valueOf(2.99));
    item.setDescription("A widget that is round");
  }

  @Test
  public void getItems_shouldReturnListOfItems() {

    when(itemRepository.findAll()).thenReturn(List.of(this.item));

    final ResponseEntity<List<Item>> response = itemController.getItems();

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    List<Item> itemList = response.getBody();
    assertNotNull(itemList);
    assertEquals(1, itemList.size());
  }

  @Test
  public void getItemById_shouldReturnItem() {
    when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(this.item));
    final ResponseEntity<Item> response = itemController.getItemById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    assertEquals("Round Widget", response.getBody().getName());
  }

  @Test
  public void getItemsByName_shouldReturnItem() {
    when(itemRepository.findByName("Round Widget")).thenReturn(List.of(this.item));
    final ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    List<Item> itemList = response.getBody();
    assertNotNull(itemList);
    assertEquals(1, itemList.size());
  }

  @Test
  public void getItemsByName_shouldReturn_NOT_FOUND() {
    when(itemRepository.findByName("Round Widget")).thenReturn(List.of(this.item));
    final ResponseEntity<List<Item>> response = itemController.getItemsByName(null);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());

  }
}
