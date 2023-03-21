package app.search.blog.repository;

import app.search.blog.entity.SearchRank;
import app.search.blog.payload.response.SearchRankMonthResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface SearchRankRepository extends JpaRepository<SearchRank, Long> {
    Optional<SearchRank> findByKeyWordAndCreatedAtBetween(String keyWord, Timestamp startDate, Timestamp endDate);

    @Query(value = "SELECT key_word AS keyWord, \n" +
            " SUM(number_of_searches) AS numberOfSearches \n" +
            " FROM search_rank \n" +
            " WHERE created_at BETWEEN ?1 AND ?2 \n" +
            " GROUP BY key_word ",
            nativeQuery = true)
    Page<SearchRankMonthResponse> findAllByCreatedBetween(Timestamp startDate, Timestamp endDate, PageRequest pageable);
}
