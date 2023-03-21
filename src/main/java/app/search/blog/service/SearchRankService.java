package app.search.blog.service;

import app.search.blog.enums.EPageSort;
import app.search.blog.payload.response.PaginationResponse;


public interface SearchRankService {
    void save(String keyWord);

    PaginationResponse getPopularityRanking(int page, int size, EPageSort sort);

    PaginationResponse getPopularityRankingMonth(String yearMonth, int page, int size, EPageSort sort);
}
