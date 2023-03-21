package app.search.webclient.service;

import app.search.blog.payload.response.PaginationResponse;
import app.search.webclient.enums.ESearchBlogSortType;
import reactor.core.publisher.Mono;

public interface WebClientService {
    Mono<PaginationResponse> findBlogBySearchWordAndSortType(String query, ESearchBlogSortType sort, int page, int size);
}
