package store.aurora.feign_client.review;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.review.dto.request.ReviewRequest;
import store.aurora.review.dto.response.ReviewResponse;

import java.util.List;

@FeignClient(name = "reviewClient", url = "${api.gateway.base-url}" + "/api/reviews")
public interface ReviewClient {

    // 리뷰 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> createReview(@RequestPart ReviewRequest request,
                                        @RequestPart(value = "files",required = false) List<MultipartFile> files,
                                        @RequestParam Long bookId,
                                        @RequestParam String userId);
//                                        @RequestParam Integer rating,
//                                        @RequestParam String content

    // 도서 ID로 리뷰 조회
    @GetMapping("/book/{bookId}")
    ResponseEntity<List<ReviewResponse>> getReviewsByBookId(@PathVariable Long bookId);

    // 사용자 ID로 리뷰 조회
    @GetMapping("/user/{userId}")
    ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable String userId);

    // 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    ReviewResponse getReviewById(@PathVariable Long reviewId);

    // 리뷰 수정
    @PutMapping(value = "/{reviewId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateReview(@PathVariable Long reviewId,
                                        @RequestPart ReviewRequest request,
                                        @RequestPart(required = false) List<MultipartFile> files,
//                                        @RequestParam Long bookId,
                                        @RequestParam String userId);


}
