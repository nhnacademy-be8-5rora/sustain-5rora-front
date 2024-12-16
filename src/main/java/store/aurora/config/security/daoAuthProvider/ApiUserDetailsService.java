package store.aurora.config.security.daoAuthProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import store.aurora.feignClient.AuthClient;
import store.aurora.user.dto.response.UserPwdAndRoleResponse;

@Component
@RequiredArgsConstructor
public class ApiUserDetailsService implements UserDetailsService {

    private final AuthClient authClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserPwdAndRoleResponse userInfo = authClient.getPasswordAndRole(username);

        return new ApiUserDetails(username, userInfo.password(), userInfo.role());
    }
}
