package app.search.blog.service;

import app.search.blog.Constants;
import app.search.blog.entity.SearchRank;
import app.search.blog.payload.response.PaginationResponse;
import app.search.blog.repository.SearchRankRepository;
import app.search.webclient.enums.ESearchBlogSortType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class BlogServiceTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private SearchRankRepository searchRankRepository;

    @Test
    @DisplayName("검색어 저장 및 블로그 검색 결과 잘 오는지 확인하기.")
    void searchBlog() {
        // Given
        String keyWord = "스프링부트 테스트 코드 작성";
        ESearchBlogSortType sort = ESearchBlogSortType.ACCURACY;
        int page = 1;
        int size = 10;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_PATTERN);
        String todayDate = simpleDateFormat.format(Timestamp.valueOf(LocalDateTime.now()));

        // When
        Mono<PaginationResponse> responseMono = blogService.searchBlog(keyWord, sort, page, size);
        PaginationResponse response = responseMono.block();
        log.info("Response : {}", response.toString());

        // Then
        Optional<SearchRank> searchRankOptional = searchRankRepository.findByKeyWordAndCreatedAtBetween(
                keyWord,
                Timestamp.valueOf(todayDate + Constants.START_TODAY_HOUR),
                Timestamp.valueOf(todayDate + Constants.END_TODAY_HOUR)
        );
        assertTrue(searchRankOptional.isPresent());
        assertEquals(page, response.getPage());
        assertTrue(response.getItemsPerPage() <= size);
    }
}