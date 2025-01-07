package store.aurora.search.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.book.dto.AuthorDTO;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.common.JwtUtil;
import store.aurora.feignClient.BookSearchClient;
import store.aurora.feignClient.UserClient;  // UserClient 임포트 추가
import store.aurora.book.CategoryService;
import store.aurora.search.dto.BookSearchResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)  // 컨트롤러만 테스트하는 설정
class SearchControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private BookSearchClient bookSearchClient;  // BookSearchClient 모킹

    @MockBean
    private CategoryService categoryService;  // CategoryService 모킹

    @MockBean
    private UserClient userClient;  // UserClient 모킹 추가

    @Autowired
    private SearchController searchController;  // SearchController에 MockBean 주입

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void testSearch() throws Exception {
        // given
        String keyword = "한강";
        String type = "title";
        String pageNum = "1";
        String orderBy = "salePrice";
        String orderDirection = "desc";

        // BookSearchResponseDTO 객체 생성
        BookSearchResponseDTO bookSearchResponseDTO = new BookSearchResponseDTO();
        bookSearchResponseDTO.setId(1L);
        bookSearchResponseDTO.setTitle("한강의 작품");
        bookSearchResponseDTO.setRegularPrice(20000);
        bookSearchResponseDTO.setSalePrice(15000);
        bookSearchResponseDTO.setPublishDate(LocalDate.of(2020, 5, 20));
        bookSearchResponseDTO.setPublisherName("한강 출판사");
        bookSearchResponseDTO.setImgPath("/images/han-gang.jpg");

        // AuthorDTO 객체 생성
        AuthorDTO author = new AuthorDTO();
        author.setName("한강");
        author.setRole("author");

        // AuthorDTO 리스트 설정
        bookSearchResponseDTO.setAuthors(Collections.singletonList(author));

        // Category ID 리스트 설정
        bookSearchResponseDTO.setCategoryIdList(Collections.singletonList(101L));

        // 나머지 필드 설정
        bookSearchResponseDTO.setViewCount(1000L);
        bookSearchResponseDTO.setReviewCount(150);
        bookSearchResponseDTO.setReviewRating(4.5);
        bookSearchResponseDTO.setLiked(true);
        bookSearchResponseDTO.setSale(true);

        // Page 객체에 값 설정 (빈 리스트 대신 실제 데이터로 설정)
        List<BookSearchResponseDTO> books = Collections.singletonList(bookSearchResponseDTO);
        Page<BookSearchResponseDTO> page = new PageImpl<>(books); // Page 객체를 생성할 때 값 설정

        // 모킹 설정
        when(bookSearchClient.searchBooksByKeyword(anyString(), eq(type), eq(keyword), eq(pageNum), eq(orderBy), eq(orderDirection)))
                .thenReturn(page); // `page` 객체를 반환하도록 설정

        // when & then
        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", pageNum)
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", books))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("keyword", keyword))
                .andExpect(model().attribute("type", type))
                .andExpect(model().attribute("orderBy", orderBy))
                .andExpect(model().attribute("orderDirection", orderDirection));

        // verify
        verify(bookSearchClient, times(1)).searchBooksByKeyword(anyString(), eq(type), eq(keyword), eq(pageNum), eq(orderBy), eq(orderDirection));
    }



    @Test
    void testSearchWithInvalidEncoding() throws Exception {
        // given
        String invalidKeyword = "한강@#";  // 예시로 잘못된 인코딩을 사용할 수 있습니다.

        // Mocking Page<BookSearchResponseDTO>
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);  // 빈 리스트로 설정
        when(bookSearchClient.searchBooksByKeyword(anyString(), eq("title"), eq(invalidKeyword), eq("1"), eq("salePrice"), eq("desc")))
                .thenReturn(null);

        // when & then
        mockMvc.perform(get("/books/search")
                        .param("keyword", invalidKeyword)
                        .param("type", "title")
                        .param("pageNum", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "검색 결과가 없습니다."));
    }

    @Test
    public void testSearchWithCategoryType() throws Exception {
        // mock 데이터 설정
        String keyword = "1"; // category ID
        String type = "category";

        CategoryDTO mockCategory = new CategoryDTO();
        mockCategory.setId(1L);
        mockCategory.setName("Category 1");

        // 페이지 요청 생성
        Pageable pageable = PageRequest.of(0, 10);  // 첫 번째 페이지, 한 페이지당 10개의 항목

        // BookSearchResponseDTO 객체 생성 (빈 목록으로 설정)
        List<BookSearchResponseDTO> books = Arrays.asList(new BookSearchResponseDTO());

        // PageImpl로 Page 객체 생성 (PageImpl 사용)
        Page<BookSearchResponseDTO> mockPage = new PageImpl<>(books, pageable, books.size());

        when(categoryService.findById(Long.parseLong(keyword))).thenReturn(mockCategory);
        when(bookSearchClient.searchBooksByKeyword(anyString(), eq(type), eq(keyword), anyString(), anyString(), anyString()))
                .thenReturn(mockPage);

        // GET 요청 시 해당 메서드 호출
        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", "1")
                        .param("orderBy", "title")
                        .param("orderDirection", "asc"))
                .andExpect(status().isOk()) // 상태 코드 200
                .andExpect(view().name("search/bookSearchResults")) // "bookSearchResults" 뷰 반환
                .andExpect(model().attributeExists("categories")) // "categories" 모델에 존재해야 함
                .andExpect(model().attribute("categories", mockCategory)); // mockCategory 값이 모델에 담겨 있는지 확인

        // CategoryService가 호출되었는지 확인
        verify(categoryService, times(1)).findById(1L);
    }

}
