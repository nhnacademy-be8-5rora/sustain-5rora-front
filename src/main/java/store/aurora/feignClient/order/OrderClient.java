package store.aurora.feignClient.order;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "orderClient", url = "${api.gateway.base-url}" + "/api/order")
public interface OrderClient {

}
