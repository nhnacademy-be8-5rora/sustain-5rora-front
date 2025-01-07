package store.aurora.feign_client.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import store.aurora.config.security.constants.SecurityConstants;

@FeignClient(name = "LikeClient", url = "${api.gateway.base-url}" + "/api/books/likes")
public interface LikeClient {

    @PostMapping("/{bookId}")
    public boolean doLike( @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken,
                                            @PathVariable("bookId") String bookId);

}
