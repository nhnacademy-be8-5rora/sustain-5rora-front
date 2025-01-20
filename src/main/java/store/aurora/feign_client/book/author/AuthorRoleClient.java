package store.aurora.feign_client.book.author;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;

@FeignClient(name = "authorRoleClient", url = "${api.gateway.base-url}" + "/api/author-roles")
public interface AuthorRoleClient {

    @GetMapping
    ResponseEntity<Page<AuthorRoleResponseDto>> getAllRoles(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size);

    @GetMapping("/{id}")
    ResponseEntity<AuthorRoleResponseDto> getRoleById(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<Void> createRole(@RequestBody AuthorRoleRequestDto requestDto);

    @PutMapping("/{id}")
    ResponseEntity<Void> updateRole(@PathVariable("id") Long id, @RequestBody AuthorRoleRequestDto requestDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRole(@PathVariable("id") Long id);
}