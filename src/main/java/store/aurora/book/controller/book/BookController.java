package store.aurora.book.controller.book;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.common.JwtUtil;
import store.aurora.feign_client.book.BookClient;
import store.aurora.search.dto.BookSearchResponseDTO;


@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookClient bookClient;

    @GetMapping("/{bookId}")
    public String singleInquiryBookInfo(@PathVariable Long bookId, Model model) {
        BookDetailsDto book = bookClient.getBookDetails(bookId).getBody();
        model.addAttribute("bookInfo", book);

        return "bookdetail-test";
    }

    @GetMapping("/likes")
    public String getLikeBooks(@RequestParam(defaultValue = "1") String pageNum,
                               Model model, HttpServletRequest request) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        if (jwt.equals("Bearer null")) {
            jwt = "";  // jwt가 null일 경우 빈 문자열 설정
        }
        ResponseEntity<Page<BookSearchResponseDTO>> likeBooks = bookClient.getLikedBooksByUser(jwt, Long.parseLong(pageNum));
        int page = Integer.parseInt(pageNum) - 1; // 페이지 번호 0-based

        Page<BookSearchResponseDTO> books = likeBooks.getBody();
        model.addAttribute("books", books.getContent());
        model.addAttribute("currentPage", page+1);  // `Long` 타입
        model.addAttribute("totalPages", books.getTotalPages());  // `Integer` 타입
        return "book/book-likes";
    }

}
