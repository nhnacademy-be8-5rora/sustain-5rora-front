package store.aurora.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import store.aurora.feign_client.search.BookSearchClient;
import store.aurora.feign_client.search.ElasticSearchClient;
import store.aurora.search.dto.BookSearchResponseDTO;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final BookSearchClient bookSearchClient;
    private final ElasticSearchClient elasticSearchClient;


    @Override
    public Page<BookSearchResponseDTO> searchBooks(String jwt, String type, String keyword, int pageNum,String orderBy,String orderDirection) {
        if("fullText".equals(type)) {
            return elasticSearchClient.searchBooks(jwt,type,keyword,pageNum);
        }
        return bookSearchClient.searchBooksByKeyword(jwt,type,keyword,pageNum,orderBy,orderDirection);
    }
}
