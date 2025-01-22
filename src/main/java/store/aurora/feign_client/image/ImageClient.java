package store.aurora.feign_client.image;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "imageClient", url = "${api.gateway.base-url}" + "/api/images")
public interface ImageClient {

    @GetMapping("/{type}/{id}/{filename}")
    ResponseEntity<Resource> getImage(@PathVariable("type") String type,
                                      @PathVariable("id") Long id,
                                      @PathVariable("filename") String filename);
}