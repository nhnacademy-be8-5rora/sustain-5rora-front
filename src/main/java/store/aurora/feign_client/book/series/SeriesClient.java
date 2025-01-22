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

    @GetMapping("/{series-id}")
    ResponseEntity<SeriesResponseDto> getSeriesById(@PathVariable("series-id") Long id);

    @PostMapping
    ResponseEntity<Void> createSeries(@RequestBody SeriesRequestDto requestDto);

    @PutMapping("/{series-id}")
    ResponseEntity<Void> updateSeries(@PathVariable("series-id") Long id, @RequestBody SeriesRequestDto requestDto);

    @DeleteMapping("/{series-id}")
    ResponseEntity<Void> deleteSeries(@PathVariable("series-id") Long id);
}