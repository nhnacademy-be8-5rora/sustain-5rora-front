package store.aurora.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import store.aurora.feign_client.image.ImageClient;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageClient imageClient;

    @GetMapping("/{type}/{id}/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable String filename) {

        return imageClient.getImage(type, id, filename);
    }
}