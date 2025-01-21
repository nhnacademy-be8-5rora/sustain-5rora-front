package store.aurora.book.controller.author;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.util.PaginationUtil;
import store.aurora.feign_client.book.author.AuthorClient;

import java.util.Optional;

@Controller
@RequestMapping("/admin/authors")
@RequiredArgsConstructor
public class AdminAuthorController {
    private static final String REDIRECT_AUTHORS = "redirect:/admin/authors";

    private final AuthorClient authorClient;

    // 작가 관리 페이지 렌더링
    @GetMapping
    public String showAuthorPage(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        ResponseEntity<Page<AuthorResponseDto>> response = authorClient.getAllAuthors(page, size);
        Page<AuthorResponseDto> authorPage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, authorPage, "authors", 5);

        return "admin/author/authors";
    }

    // 작가 추가 처리
    @PostMapping("/create")
    public String addAuthor(@Valid @ModelAttribute AuthorRequestDto authorRequestDto) {
        authorClient.createAuthor(authorRequestDto);
        return REDIRECT_AUTHORS;
    }

    // 작가 수정 처리
    @PostMapping("/update/{id}")
    public String updateAuthor(@PathVariable("id") Long id, @Valid @ModelAttribute AuthorRequestDto authorRequestDto) {
        authorClient.updateAuthor(id, authorRequestDto);
        return REDIRECT_AUTHORS;
    }

    // 작가 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable("id") Long id) {
        authorClient.deleteAuthor(id);
        return REDIRECT_AUTHORS;
    }
}