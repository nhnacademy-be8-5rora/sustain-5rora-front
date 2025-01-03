package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import store.aurora.address.dto.UserAddressDTO;
import store.aurora.address.dto.UserAddressRequest;
import store.aurora.config.security.constants.SecurityConstants;

import java.util.List;

@FeignClient(name = "addressClient", url = "${api.gateway.base-url}" + "/api/addresses")
public interface AddressClient {
    @GetMapping
    List<UserAddressDTO> getUserAddresses(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @PostMapping
    void addUserAddress(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken,
                        @RequestBody UserAddressRequest request);

    @GetMapping("/{id}")
    UserAddressDTO getAddressById(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken,
                                  @PathVariable("id") Long id);

    @PutMapping("/{id}")
    void updateAddress(@PathVariable("id") Long id,
                       @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken,
                       @RequestBody UserAddressRequest request);

    @DeleteMapping("/{id}")
    void deleteUserAddress(@PathVariable("id") Long id,
                           @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);
}