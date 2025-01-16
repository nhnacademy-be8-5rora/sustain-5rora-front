package store.aurora.feign_client.book.series;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.series.SeriesRequestDto;
import store.aurora.book.dto.series.SeriesResponseDto;

@FeignClient(name = "seriesClient", url = "${api.gateway.base-url}" + "/api/series")
public interface SeriesClient {

    @GetMapping
    ResponseEntity<Page<SeriesResponseDto>> getAllSeries(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size);

    @GetMapping("/{id}")
    ResponseEntity<SeriesResponseDto> getSeriesById(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<Void> createSeries(@RequestBody SeriesRequestDto requestDto);

    @PutMapping("/{id}")
    ResponseEntity<Void> updateSeries(@PathVariable("id") Long id, @RequestBody SeriesRequestDto requestDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSeries(@PathVariable("id") Long id);
}