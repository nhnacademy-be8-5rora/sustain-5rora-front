package store.aurora.cart.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import store.aurora.cart.dto.CartItemDTO;
import store.aurora.feign_client.CartClient;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartClient cartClient;

    @InjectMocks
    private CartController cartController;

    CartItemDTO cartItemDTO;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setViewResolvers(viewResolver)
                .build();

        cartItemDTO = new CartItemDTO(1L, 2);
    }

    @Test
    void testGetCartPage() throws Exception {
        // Arrange
        Map<String, Object> cartData = Map.of("items", List.of(cartItemDTO), "totalPrice", 100);
        when(cartClient.getCart()).thenReturn(new ResponseEntity<>(cartData, HttpStatus.OK));

        // Act & Assert
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("cart", cartData));
    }

    @Test
    void testAddCart() throws Exception {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("Item added", HttpStatus.OK);
        when(cartClient.addCartItem(any(CartItemDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/cart")
                        .param("bookId", "1")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartClient, times(1)).addCartItem(any(CartItemDTO.class));
    }

    @Test
    void testDeleteItemToCart() throws Exception {
        // Arrange
        Long bookId = 1L;
        ResponseEntity<String> response = new ResponseEntity<>("Item deleted", HttpStatus.OK);
        when(cartClient.deleteCartItem(bookId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/cart/{bookId}", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartClient, times(1)).deleteCartItem(bookId);
    }

    @Test
    void testAddCartWithError() throws Exception {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        when(cartClient.addCartItem(any(CartItemDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/cart")
                        .param("bookId", "1")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));

        verify(cartClient, times(1)).addCartItem(any(CartItemDTO.class));
    }
}