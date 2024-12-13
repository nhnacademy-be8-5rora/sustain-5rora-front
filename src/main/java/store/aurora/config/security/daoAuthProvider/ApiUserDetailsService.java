package store.aurora.config.security.daoAuthProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import store.aurora.feignClient.UserClient;
import store.aurora.user.dto.response.UserPwdAndRoleResponse;

@Component
@RequiredArgsConstructor
public class ApiUserDetailsService implements UserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserPwdAndRoleResponse userInfo = userClient.getPasswordAndRole(username);

        return new ApiUserDetails(username, userInfo.password(), userInfo.role());
    }
}
