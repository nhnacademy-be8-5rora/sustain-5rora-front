package store.aurora.book.controller.author;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;
import store.aurora.book.util.PaginationUtil;
import store.aurora.feign_client.book.author.AuthorRoleClient;

import java.util.Optional;

@Controller
@RequestMapping("/admin/author-roles")
@RequiredArgsConstructor
public class AdminAuthorRoleController {
    private static final String REDIRECT_AUTHOR_ROLES = "redirect:/admin/author-roles";

    private final AuthorRoleClient authorRoleClient;

    // 작가 역할 관리 페이지 렌더링
    @GetMapping
    public String showAuthorRolePage(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     Model model) {
        ResponseEntity<Page<AuthorRoleResponseDto>> response = authorRoleClient.getAllRoles(page, size);
        Page<AuthorRoleResponseDto> rolePage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, rolePage, "authorRoles", 5);

        return "admin/author/author_roles";
    }

    // 작가 역할 추가 처리
    @PostMapping("/create")
    public String addAuthorRole(@Valid @ModelAttribute AuthorRoleRequestDto authorRoleRequestDto) {
        authorRoleClient.createRole(authorRoleRequestDto);
        return REDIRECT_AUTHOR_ROLES;
    }

    // 작가 역할 수정 처리
    @PostMapping("/update/{id}")
    public String updateAuthorRole(@PathVariable("id") Long id, @Valid @ModelAttribute AuthorRoleRequestDto authorRoleRequestDto) {
        authorRoleClient.updateRole(id, authorRoleRequestDto);
        return REDIRECT_AUTHOR_ROLES;
    }

    // 작가 역할 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteAuthorRole(@PathVariable("id") Long id) {
        authorRoleClient.deleteRole(id);
        return REDIRECT_AUTHOR_ROLES;
    }
}