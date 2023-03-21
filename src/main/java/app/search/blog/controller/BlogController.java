package app.search.blog.controller;

import app.search.blog.Constants;
import app.search.blog.payload.response.PaginationResponse;
import app.search.webclient.enums.ESearchBlogSortType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import app.search.blog.service.BlogService;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
@Validated
public class BlogController {

    private final BlogService blogService;

    @Operation(summary = "키워드를 통해 블로그 검색")
    @GetMapping("/search")
    public ResponseEntity<Mono<PaginationResponse>> searchBlog(@RequestParam(value = "query") String query,
                                                               @RequestParam(value = "sort", required = false) ESearchBlogSortType sort,
                                                               @RequestParam(value = "page", required = false, defaultValue = Constants.SEARCH_DEFAULT_PAGE) @Min(1) @Max(50) int page,
                                                               @RequestParam(value = "size", required = false, defaultValue = Constants.SEARCH_DEFAULT_SIZE) @Min(1) @Max(50) int size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(blogService.searchBlog(query, sort, page, size));
    }
}
