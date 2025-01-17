package store.aurora.book.controller.publisher;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.publisher.PublisherRequestDto;
import store.aurora.book.dto.publisher.PublisherResponseDto;
import store.aurora.book.util.PaginationUtil;
import store.aurora.feign_client.book.publisher.PublisherClient;

import java.util.Optional;

@Controller
@RequestMapping("/admin/publishers")
@RequiredArgsConstructor
public class AdminPublisherController {
    private static final String REDIRECT_PUBLISHERS = "redirect:/admin/publishers";

    private final PublisherClient publisherClient;

    // 출판사 관리 페이지 렌더링
    @GetMapping
    public String showPublisherPage(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    Model model) {
        ResponseEntity<Page<PublisherResponseDto>> response = publisherClient.getAllPublishers(page,size);
        Page<PublisherResponseDto> publisherPage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, publisherPage, "publishers", 5);

        return "admin/publisher/publishers";
    }

    // 출판사 추가 처리
    @PostMapping("/create")
    public String addPublisher(@Valid @ModelAttribute PublisherRequestDto publisherRequestDto) {
        publisherClient.createPublisher(publisherRequestDto);
        return REDIRECT_PUBLISHERS;
    }

    // 출판사 수정 처리
    @PostMapping("/update/{id}")
    public String updatePublisher(@PathVariable("id") Long id, @Valid @ModelAttribute PublisherRequestDto publisherRequestDto) {
        publisherClient.updatePublisher(id, publisherRequestDto);
        return REDIRECT_PUBLISHERS;
    }

    // 출판사 삭제 처리
    @PostMapping("/delete/{id}")
    public String deletePublisher(@PathVariable("id") Long id) {
        publisherClient.deletePublisher(id);
        return REDIRECT_PUBLISHERS;
    }
}