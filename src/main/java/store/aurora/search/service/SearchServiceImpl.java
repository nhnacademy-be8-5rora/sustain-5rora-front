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
    public Page<BookSearchResponseDTO> searchBooks(String jwt, String type, String keyword, int pageNum, String orderBy, String orderDirection) {
        try {
            // orderBy가 존재하면 bookSearchClient를 호출
            if (orderBy != null && !orderBy.isEmpty()) {
                return bookSearchClient.searchBooksByKeyword(jwt, type, keyword, pageNum, orderBy, orderDirection);
            }

            // orderBy가 없으면 ElasticSearch를 호출
            Page<BookSearchResponseDTO> elasticResult = elasticSearchClient.searchBooks(jwt, type, keyword, pageNum);

            // ElasticSearch 결과가 비거나 null이면 bookSearchClient 호출
            if (elasticResult == null || elasticResult.isEmpty()) {
                return bookSearchClient.searchBooksByKeyword(jwt, type, keyword, pageNum, orderBy, orderDirection);
            }

            return elasticResult;
        } catch (Exception e) {
            // 예외 발생 시 bookSearchClient 호출
            return bookSearchClient.searchBooksByKeyword(jwt, type, keyword, pageNum, orderBy, orderDirection);
        }
    }


}
