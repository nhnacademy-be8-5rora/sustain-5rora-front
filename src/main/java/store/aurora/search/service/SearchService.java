package store.aurora.search.service;

import org.springframework.data.domain.Page;

import store.aurora.search.dto.BookSearchResponseDTO;

public interface SearchService {

    Page<BookSearchResponseDTO> searchBooks(String jwt,String type,String keyword,int pageNum,String orderBy,String orderDirection);
}
