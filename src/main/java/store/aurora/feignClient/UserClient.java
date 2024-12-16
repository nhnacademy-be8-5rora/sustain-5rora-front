package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "userClient", url = "${api.gateway.base-url}" + "/api/users")
public interface UserClient {


}
