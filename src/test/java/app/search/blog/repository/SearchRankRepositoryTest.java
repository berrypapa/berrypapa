package app.search.blog.repository;

import app.search.blog.entity.SearchRank;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Slf4j
class SearchRankRepositoryTest {

    @Autowired
    private SearchRankRepository searchRankRepository;

    @Test
    @DisplayName("검색어 저장 잘 되는지 확인하기.")
    void save() {
        // Given
        SearchRank searchRank = new SearchRank("은행");

        int targetNumberOfSearches = 0;
        for (int i = 0; i < 2; i++) {
            searchRank.addCount();
            targetNumberOfSearches++;
        }

        SearchRank savedSearchRank = searchRankRepository.save(searchRank);
        log.info("Saved search rank info : {}", savedSearchRank);

        // When
        List<SearchRank> searchRankList = searchRankRepository.findAll();

        // Then
        assertEquals(targetNumberOfSearches, savedSearchRank.getNumberOfSearches());
        assertTrue(searchRankList.contains(savedSearchRank));
    }
}