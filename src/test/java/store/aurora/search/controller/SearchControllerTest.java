package store.aurora.search.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.feign_client.book.CategoryClient;
import store.aurora.feign_client.search.BookSearchClient;
import store.aurora.feign_client.search.ElasticSearchClient;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchControllerTest {

    @InjectMocks
    private SearchController searchController;

    @Mock
    private BookSearchClient bookSearchClient;

    @Mock
    private ElasticSearchClient elasticSearchClient;

    @Mock
    private CategoryClient categoryClient;

    @Mock
    private SearchService searchService;

    @Mock
    private Model model;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void search_ShouldReturnBooks_WhenValidRequest() throws Exception {
        // Arrange
        String keyword = "한강";
        String type = "title";
        String pageNum = "1";
        String orderBy = "salePrice";
        String orderDirection = "desc";

        BookSearchResponseDTO book1 = new BookSearchResponseDTO();
        book1.setTitle("한강의 책");
        List<BookSearchResponseDTO> books = Arrays.asList(book1);
        // Mocking the searchService response
        when(searchService.searchBooks(anyString(), eq(type), eq(keyword), eq(0), eq(orderBy), eq(orderDirection)))
                .thenReturn(new PageImpl<>(books));

        // Mocking categoryClient response
        ResponseEntity<List<CategoryResponseDTO>> categoryResponse = ResponseEntity.ok(Collections.emptyList());
        when(categoryClient.getCategories()).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", pageNum)
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection)
                        .sessionAttr("jwt", "Bearer someJwtToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", books));
    }

    @Test
    void search_ShouldReturnErrorMessage_WhenNoBooksFound() throws Exception {
        // Arrange
        String keyword = "없는 책";
        String type = "title";
        String pageNum = "1";
        String orderBy = "salePrice";
        String orderDirection = "desc";

        // Mocking the searchService response for no books found
        when(searchService.searchBooks(anyString(), eq(type), eq(keyword), eq(0), eq(orderBy), eq(orderDirection)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act & Assert
        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", pageNum)
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "\"" + keyword + "\"로 검색된 결과가 없습니다."));
    }

    @Test
    void search_ShouldReturnErrorPage_WhenInvalidPageNumber() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/books/search")
                        .param("pageNum", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "잘못된 페이지 번호입니다."));
    }
}
