package app.search.blog.service;

import app.search.blog.payload.response.PaginationResponse;
import app.search.webclient.enums.ESearchBlogSortType;
import reactor.core.publisher.Mono;


public interface BlogService {
    Mono<PaginationResponse> searchBlog(String query, ESearchBlogSortType sort, int page, int size);
}
