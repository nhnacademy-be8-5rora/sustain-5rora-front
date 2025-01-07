package store.aurora.book.controller.tag;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.feign_client.book.tag.TagClient;

import java.util.Collections;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagClient tagClient;

    // 태그 관리 페이지 렌더링
    @GetMapping
    public String showTagManagementPage(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "2") int size,
                                        Model model) {
        ResponseEntity<Page<TagResponseDto>> response = tagClient.getTags(page,size);
        Page<TagResponseDto> tagPage = response.getBody();


        if (tagPage != null) {
            model.addAttribute("tags", tagPage.getContent());
            model.addAttribute("currentPage", tagPage.getNumber());
            model.addAttribute("totalPages", tagPage.getTotalPages());
        } else {
            model.addAttribute("tags", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
        }
        return "admin/tag/tags";
    }

    // 태그 추가 처리
    @PostMapping("/create")
    public String addTag(@ModelAttribute TagRequestDto tagRequestDto) {
        tagClient.createTag(tagRequestDto);
        return "redirect:/tags";
    }

    // 태그 수정 처리
    @PostMapping("/update/{id}")
    public String updateTag(@PathVariable("id") Long id, @ModelAttribute TagRequestDto tagRequestDto) {
        tagClient.updateTag(id, tagRequestDto);
        return "redirect:/tags";
    }

    // 태그 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteTag(@PathVariable("id") Long id) {
        tagClient.deleteTag(id);
        return "redirect:/tags";
    }

}
