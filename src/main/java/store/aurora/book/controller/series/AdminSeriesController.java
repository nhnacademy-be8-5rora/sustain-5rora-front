package store.aurora.book.controller.series;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.series.SeriesRequestDto;
import store.aurora.book.dto.series.SeriesResponseDto;
import store.aurora.book.util.PaginationUtil;
import store.aurora.feign_client.book.series.SeriesClient;

import java.util.Optional;

@Controller
@RequestMapping("/admin/series")
@RequiredArgsConstructor
public class AdminSeriesController {
    private static final String REDIRECT_SERIES = "redirect:/admin/series";

    private final SeriesClient seriesClient;

    // 시리즈 관리 페이지 렌더링
    @GetMapping
    public String showSeriesPage(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        ResponseEntity<Page<SeriesResponseDto>> response = seriesClient.getAllSeries(page, size);
        Page<SeriesResponseDto> seriesPage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, seriesPage, "seriesList", 5);

        return "admin/series/series";
    }

    // 시리즈 추가 처리
    @PostMapping("/create")
    public String addSeries(@Valid @ModelAttribute SeriesRequestDto seriesRequestDto) {
        seriesClient.createSeries(seriesRequestDto);
        return REDIRECT_SERIES;
    }

    // 시리즈 수정 처리
    @PostMapping("/update/{id}")
    public String updateSeries(@PathVariable("id") Long id, @Valid @ModelAttribute SeriesRequestDto seriesRequestDto) {
        seriesClient.updateSeries(id, seriesRequestDto);
        return REDIRECT_SERIES;
    }

    // 시리즈 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteSeries(@PathVariable("id") Long id) {
        seriesClient.deleteSeries(id);
        return REDIRECT_SERIES;
    }
}