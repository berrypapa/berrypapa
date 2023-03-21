package app.search.blog.service.impl;

import app.search.blog.payload.response.PaginationResponse;
import app.search.blog.service.BlogService;
import app.search.blog.service.SearchRankService;
import app.search.webclient.enums.ESearchBlogSortType;
import app.search.webclient.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Slf4j
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final WebClientService webClientService;
    private final SearchRankService searchRankService;

    @Override
    public Mono<PaginationResponse> searchBlog(String query, ESearchBlogSortType sort, int page, int size) {
        log.info("블로그 검색 요청이 들어왔습니다.");
        ESearchBlogSortType validatedSort = sort == null ? ESearchBlogSortType.ACCURACY : sort;

        searchRankService.save(query);

        // return webClientService.findKakaoBlogBySearchWordAndSortType(query, validatedSort, page, size);
        return webClientService.findBlogBySearchWordAndSortType(query, validatedSort, page, size);
    }
}
