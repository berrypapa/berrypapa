package app.search.blog.controller;

import app.search.blog.Constants;
import app.search.blog.enums.EPageSort;
import app.search.blog.payload.response.PaginationResponse;
import app.search.blog.service.SearchRankService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/popularity")
@Validated
public class SearchRankController {

    private final SearchRankService searchRankService;

    @Operation(summary = "인기 검색어 리스트 조회")
    @GetMapping("/ranking")
    public ResponseEntity<PaginationResponse> getPopularityRanking(@RequestParam(value = "page", required = false, defaultValue = Constants.SEARCH_DEFAULT_PAGE) @Min(1) @Max(50) int page,
                                                                   @RequestParam(value = "size", required = false, defaultValue = Constants.SEARCH_DEFAULT_SIZE) @Min(1) @Max(10) int size,
                                                                   @RequestParam(value = "sort", required = false) EPageSort sort
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(searchRankService.getPopularityRanking(page, size, sort));
    }

    @Operation(summary = "지정한 연도와 달의(yyyy-MM) 인기 검색어 리스트 조회")
    @GetMapping("/ranking/{yearMonth}")
    public ResponseEntity<PaginationResponse> getPopularityRankingMonth(@PathVariable("yearMonth") @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])") String yearMonth,
                                                                        @RequestParam(value = "page", required = false, defaultValue = Constants.SEARCH_DEFAULT_PAGE) @Min(1) @Max(50) int page,
                                                                        @RequestParam(value = "size", required = false, defaultValue = Constants.SEARCH_DEFAULT_SIZE) @Min(1) @Max(10) int size,
                                                                        @RequestParam(value = "sort", required = false) EPageSort sort
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(searchRankService.getPopularityRankingMonth(yearMonth, page, size, sort));
    }
}
