package store.aurora.book.controller.tag;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.feignClient.book.tag.TagClient;

import java.util.List;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagClient tagClient;

    // 태그 관리 페이지 렌더링
    @GetMapping
    public String showTagManagementPage(Model model) {
        ResponseEntity<List<TagResponseDto>> response = tagClient.getAllTags();
        model.addAttribute("tags", response.getBody());
        return "admin/tag/tags"; // 태그 관리 페이지 템플릿 경로
    }

    // 태그 추가 처리
    @PostMapping("/create")
    public String addTag(@ModelAttribute TagRequestDto tagRequestDto) {
        tagClient.createTag(tagRequestDto);
        return "redirect:/tags"; // 태그 추가 후 태그 관리 페이지로 리다이렉트
    }

    // 태그 수정 처리
    @PostMapping("/update/{id}")
    public String updateTag(@PathVariable("id") Long id, @ModelAttribute TagRequestDto tagRequestDto) {
        tagClient.updateTag(id, tagRequestDto);
        return "redirect:/tags"; // 태그 수정 후 태그 관리 페이지로 리다이렉트
    }

    // 태그 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteTag(@PathVariable("id") Long id) {
        tagClient.deleteTag(id);
        return "redirect:/tags"; // 태그 삭제 후 태그 관리 페이지로 리다이렉트
    }
}
