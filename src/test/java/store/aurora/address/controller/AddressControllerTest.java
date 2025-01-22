package store.aurora.address.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.address.dto.UserAddressDTO;
import store.aurora.address.dto.UserAddressRequest;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.AddressClient;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AddressClient addressFeignClient;

    @InjectMocks
    private AddressController addressController;

    String jwt = SecurityConstants.BEARER_TOKEN_PREFIX + "test-jwt";
    Cookie jwtCookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, "test-jwt");

    Long id = 1L;

    UserAddressDTO validDTO;
    UserAddressDTO validDTO2;

    UserAddressRequest request = new UserAddressRequest();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();

        validDTO = new UserAddressDTO();
        validDTO.setId(id);
        validDTO.setNickname("Home");
        validDTO.setReceiver("John Doe");
        validDTO.setRoadAddress("123 Main St");
        validDTO.setAddrDetail("Apt 101");

        validDTO2 = new UserAddressDTO();
        validDTO2.setId(2L);
        validDTO2.setNickname("Home2");
        validDTO2.setReceiver("John Doe2");
        validDTO2.setRoadAddress("123 Main St2");
        validDTO2.setAddrDetail("Apt 1012");

        request.setNickname("Home");
        request.setReceiver("John Doe");
        request.setRoadAddress("123 Main St");
        request.setAddrDetail("Apt 101");
    }

    @Test
    void testShowAddresses() throws Exception {
        // Arrange
        List<UserAddressDTO> addresses = Arrays.asList(validDTO, validDTO2);

        when(addressFeignClient.getUserAddresses(jwt)).thenReturn(addresses);

        // Act & Assert
        mockMvc.perform(get("/mypage/addresses").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/address-list"))
                .andExpect(model().attributeExists("addresses"))
                .andExpect(model().attribute("addresses", addresses));
    }

    @Test
    void testShowAddressForm() throws Exception {
        mockMvc.perform(get("/mypage/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/address-form"))
                .andExpect(model().attributeExists("address"));
    }

    @Test
    void testAddAddress() throws Exception {
        // Arrange
        doNothing().when(addressFeignClient).addUserAddress(jwt, request);

        // Act & Assert
        mockMvc.perform(post("/mypage/addresses/new")
                        .cookie(jwtCookie)
                        .param("nickname", "Home")
                        .param("receiver", "John Doe")
                        .param("roadAddress", "123 Main St")
                        .param("addrDetail", "Apt 101"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/addresses"));

        verify(addressFeignClient, times(1)).addUserAddress(jwt, request);
    }

    @Test
    void testShowEditForm() throws Exception {
        // Arrange
        when(addressFeignClient.getAddressById(jwt, id)).thenReturn(validDTO);

        // Act & Assert
        mockMvc.perform(get("/mypage/addresses/{id}", id).cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/address-form"))
                .andExpect(model().attributeExists("address"))
                .andExpect(model().attribute("address", validDTO));
    }

    @Test
    void testDeleteAddress() throws Exception {
        // Arrange
        doNothing().when(addressFeignClient).deleteUserAddress(id, jwt);

        // Act & Assert
        mockMvc.perform(post("/mypage/addresses/delete")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", String.valueOf(id)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/addresses"));

        verify(addressFeignClient, times(1)).deleteUserAddress(id, jwt);
    }

    @Test
    void testUpdateAddress() throws Exception {
        // Arrange
        doNothing().when(addressFeignClient).updateAddress(id, jwt, request);

        // Act & Assert
        mockMvc.perform(post("/mypage/addresses/{id}", id)
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nickname", "Home")
                        .param("receiver", "John Doe")
                        .param("roadAddress", "123 Main St")
                        .param("addrDetail", "Apt 101"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/addresses"));

        verify(addressFeignClient, times(1)).updateAddress(id, jwt, request);
    }
}