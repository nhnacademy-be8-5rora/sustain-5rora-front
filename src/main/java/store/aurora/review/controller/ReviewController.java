package store.aurora.review.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.feign_client.review.ReviewClient;
import store.aurora.review.dto.request.ReviewRequest;
import store.aurora.review.dto.response.ReviewResponse;

import java.util.List;

@Controller
@RequestMapping("/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewClient reviewClient;

    // 리뷰 등록 페이지
    @GetMapping("/{bookId}/write")
    public String writeReview(@PathVariable Long bookId) {
        // 리뷰 작성 페이지로 이동
        return "review/review-form";
    }

    // 리뷰 등록 처리
    @PostMapping(value = "/{bookId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createReview(Authentication authentication,
                               @PathVariable Long bookId,
                               @Valid @ModelAttribute ReviewRequest reviewRequest,
//                               @RequestParam("rating") Integer rating,
//                               @RequestParam("content") String content,
                               @RequestPart(value = "files", required = false) List<MultipartFile> files,
                               HttpServletResponse response) {
        String userId = authentication.getPrincipal().toString();

        ResponseEntity<String> clientResponse = reviewClient.createReview(reviewRequest, files, bookId, userId);

        addCookiesFromResponse(response, clientResponse);

        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            return "redirect:/books/" + bookId; // 성공 시 도서 페이지로 리디렉션
        } else {
            return "redirect:/error"; // 실패 시 에러 페이지로 이동
        }
    }

    // 도서 ID로 리뷰 조회
    @GetMapping("/book/{bookId}")
    public String getReviewsByBookId(@PathVariable Long bookId, Model model) {
        ResponseEntity<List<ReviewResponse>> clientResponse = reviewClient.getReviewsByBookId(bookId);
        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("reviews", clientResponse.getBody());
            return "review/list"; // 리뷰 목록 페이지로 이동
        } else {
            return "redirect:/error"; // 실패 시 에러 페이지로 이동
        }
    }

    // 사용자 ID로 리뷰 조회
    @GetMapping("/user/{userId}")
    public String getReviewsByUserId(@PathVariable String userId, Model model) {
        ResponseEntity<List<ReviewResponse>> clientResponse = reviewClient.getReviewsByUserId(userId);
        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("reviews", clientResponse.getBody());
            return "review/my-reviews"; // 사용자의 리뷰 목록 페이지로 이동
        } else {
            return "redirect:/error"; // 실패 시 에러 페이지로 이동
        }
    }

    // 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    public ReviewResponse getReviewById(@PathVariable Long reviewId) {
        return reviewClient.getReviewById(reviewId);
    }
//    public String getReviewById(@PathVariable Long reviewId, Model model) {
//        ReviewResponse reviewResponse = reviewClient.getReviewById(reviewId);
//        model.addAttribute("review", reviewResponse);
//        return "review/detail"; // 리뷰 상세 페이지로 이동
//    }


    // 리뷰 수정 처리
    @PostMapping(value = "/{reviewId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateReview(Authentication authentication,
                               @PathVariable Long reviewId,
                               @Valid @ModelAttribute ReviewRequest reviewRequest,
//                               @RequestParam("rating") Integer rating,
//                               @RequestParam("content") String content,
                               @RequestPart(value = "files", required = false) List<MultipartFile> files,
                               HttpServletResponse response) {
        String userId = authentication.getPrincipal().toString();

        ResponseEntity<String> clientResponse = reviewClient.updateReview(reviewId, reviewRequest, files, userId);

        addCookiesFromResponse(response, clientResponse);

        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            return "redirect:/review/user/" + userId; // 성공 시 사용자 페이지로 리디렉션
        } else {
            return "redirect:/error"; // 실패 시 에러 페이지로 이동
        }
    }

    // 쿠키 처리 메서드
    private void addCookiesFromResponse(HttpServletResponse response, ResponseEntity<?> clientResponse) {
        HttpHeaders headers = clientResponse.getHeaders();
        String sessionCookie = headers.getFirst("Set-Cookie");

        if (sessionCookie != null) {
            response.addHeader("Set-Cookie", sessionCookie);
        }
    }


//    private String processClientRes(HttpServletResponse response, ResponseEntity<String> clientResponse, Long bookId) {
//        HttpHeaders headers = clientResponse.getHeaders();
//        String sessionCookie = headers.getFirst("Set-Cookie");
//
//        if (sessionCookie != null) {
//            response.addHeader("Set-Cookie", sessionCookie);
//        }
//
//        // 성공 시 리뷰 목록으로 리디렉션, 실패 시 에러 페이지로 이동
//        if (clientResponse.getStatusCode().is2xxSuccessful()) {
//            if (bookId != null) {
//                return "redirect:/books/" + bookId;
//            } else {
//                return "redirect:/user/" ;
//            }
//        } else {
//            return "redirect:/error";
//        }
//    }

}
