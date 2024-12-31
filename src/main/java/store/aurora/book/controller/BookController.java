package store.aurora.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.category.BookCategoryDto;
import store.aurora.feignClient.book.BookClient;

import java.util.ArrayList;
import java.util.List;

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
}
