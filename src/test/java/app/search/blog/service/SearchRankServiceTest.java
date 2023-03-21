package app.search.blog.service;

import app.search.blog.entity.SearchRank;
import app.search.blog.enums.EPageSort;
import app.search.blog.payload.response.PaginationResponse;
import app.search.blog.repository.SearchRankRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class SearchRankServiceTest {

    @Autowired
    private SearchRankService searchRankService;

    private final static int totalData = 100;

    @BeforeAll
    static void beforeAll(@Autowired SearchRankRepository searchRankRepository) {
        int sequence = 1;
        for(int i = 0; i < totalData; i++) {
            String keyWord = String.format("테스트 검색어 %d", i);
            SearchRank searchRank = new SearchRank(keyWord);
            searchRank.setNumberOfSearches(i);
            searchRankRepository.save(searchRank);
            if (i % 2 == 0) {
                if (i % 10 == 0) sequence++;
                Timestamp oldDate = Timestamp.valueOf(LocalDateTime.now().minusMonths(sequence));
                searchRank.setCreatedAt(oldDate);
                searchRankRepository.save(searchRank);
            }
        }
    }

    @Test
    @DisplayName("전체 검색어 인기순위 가져오기.")
    void getPopularityRanking() {
        // Given
        int page = 1;
        int size = 10;
        EPageSort sort = EPageSort.DESCENDING;

        // When
        PaginationResponse response = searchRankService.getPopularityRanking(page, size, sort);
        log.info("Check response : {}", response.toString());

        // Then
        assertEquals(totalData, response.getTotal());
        assertEquals(page, response.getPage());
        assertEquals(size, response.getItemsPerPage());
        assertEquals((page * size < response.getTotal()), response.isHasNextPage());
    }

    @Test
    @DisplayName("지정한 yyyy-MM 형식으로 지난 달의 검색어 인기순위 가져오기.")
    void getPopularityRankingMonth() {
        // Given
        LocalDateTime towMonthAgoDateTime = LocalDateTime.now().minusMonths(2);
        String towMonthAgoYearMonth = String.format(
                "%d-%d", towMonthAgoDateTime.getYear(), towMonthAgoDateTime.getMonthValue());

        int page = 1;
        int size = 10;
        EPageSort sort = EPageSort.DESCENDING;

        // When
        PaginationResponse response = searchRankService.getPopularityRankingMonth(towMonthAgoYearMonth, page, size, sort);
        log.info("Check response : {}", response.toString());

        // Then
        assertEquals(page, response.getPage());
        assertTrue(response.getItemsPerPage() <= size);
        assertEquals((page * size < response.getTotal()), response.isHasNextPage());
    }
}