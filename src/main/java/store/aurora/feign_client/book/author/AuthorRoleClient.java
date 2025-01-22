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

    @GetMapping("/{author-role-id}")
    ResponseEntity<AuthorRoleResponseDto> getRoleById(@PathVariable("author-role-id") Long id);

    @PostMapping
    ResponseEntity<Void> createRole(@RequestBody AuthorRoleRequestDto requestDto);

    @PutMapping("/{author-role-id}")
    ResponseEntity<Void> updateRole(@PathVariable("author-role-id") Long id, @RequestBody AuthorRoleRequestDto requestDto);

    @DeleteMapping("/{author-role-id}")
    ResponseEntity<Void> deleteRole(@PathVariable("author-role-id") Long id);
}