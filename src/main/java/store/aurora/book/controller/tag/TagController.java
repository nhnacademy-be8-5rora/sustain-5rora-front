package store.aurora.book.controller.tag;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.feignClient.book.tag.TagClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagClient tagClient;

    @PostMapping
    public String createTag(@Valid @RequestBody TagRequestDto tagRequestDto) {
        tagClient.createTag(tagRequestDto);
        return "";
    }

    @DeleteMapping("/{tagId}")
    public String removeTage(@PathVariable Long tagId) {
        tagClient.removeTag(tagId);
        return "";
    }

    @PostMapping("/book-tag/")
    public String addBookTag(@RequestBody BookTagRequestDto requestDto) {
        tagClient.addBookTag(requestDto);
        return "";
    }

    @DeleteMapping("/book-tag/{bookTagId}")
    public String removeBookTag(@PathVariable Long bookTagId) {
        tagClient.removeBookTag(bookTagId);
        return "";
    }

}
