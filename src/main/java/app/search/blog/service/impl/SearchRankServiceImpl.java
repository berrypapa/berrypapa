package app.search.blog.service.impl;

import app.search.blog.Constants;
import app.search.blog.payload.response.SearchRankMonthResponse;
import app.search.blog.enums.EPageSort;
import app.search.blog.entity.SearchRank;
import app.search.blog.payload.response.PaginationResponse;
import app.search.blog.payload.response.SearchRankResponse;
import app.search.blog.repository.SearchRankRepository;
import app.search.blog.service.SearchRankService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SearchRankServiceImpl implements SearchRankService {

    private final SearchRankRepository searchRankRepository;

    @Override
    public void save(String keyWord) {
        log.info("블로그 검색 키워드를 저장 합니다.");
        SearchRank searchRank = findByKeyWordAndToday(keyWord);

        searchRank.addCount();

        searchRankRepository.save(searchRank);
    }

    @Override
    public PaginationResponse getPopularityRanking(int page, int size, EPageSort sort) {
        log.info("인기 검색어 리스트 조회 요청이 들어왔습니다.");
        PageRequest pageRequest = getPageRequest(page, size, sort);

        Page<SearchRankMonthResponse> pageSearchRankMonthResponse = searchRankRepository.findAllByCreatedBetween(
                Timestamp.valueOf(Constants.START_EARTH_DATE_TIME),
                Timestamp.valueOf(Constants.END_EARTH_DATE_TIME),
                pageRequest
        );

        ArrayList<SearchRankResponse> items = getContentsFromSearchRankMonth(pageSearchRankMonthResponse.getContent(), sort);

        return buildPaginationResponse(pageSearchRankMonthResponse, items);
    }

    @Override
    public PaginationResponse getPopularityRankingMonth(String yearMonth, int page, int size, EPageSort sort) {
        String validatedYearMonth = validationYearMonth(yearMonth);
        log.info("{}의 인기 검색어 리스트 조회 요청이 들어왔습니다.", validatedYearMonth);

        PageRequest pageRequest = getPageRequest(page, size, sort);

        Page<SearchRankMonthResponse> pageSearchRankMonthResponse = searchRankRepository.findAllByCreatedBetween(
                Timestamp.valueOf(validatedYearMonth + Constants.START_MONTH_DAY_HOUR),
                Timestamp.valueOf(validatedYearMonth + Constants.END_MONTH_DAY_HOUR),
                pageRequest
        );

        ArrayList<SearchRankResponse> items = getContentsFromSearchRankMonth(pageSearchRankMonthResponse.getContent(), sort);

        return buildPaginationResponse(pageSearchRankMonthResponse, items);
    }

    private SearchRank findByKeyWordAndToday(String keyWord) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_PATTERN);
        String todayDate = simpleDateFormat.format(Timestamp.valueOf(LocalDateTime.now()));

        return searchRankRepository.findByKeyWordAndCreatedAtBetween(
                keyWord,
                Timestamp.valueOf(todayDate + Constants.START_TODAY_HOUR),
                Timestamp.valueOf(todayDate + Constants.END_TODAY_HOUR)
        ).orElseGet(() -> searchRankRepository.save(new SearchRank(keyWord)));
    }

    private PageRequest getPageRequest(int page, int size, EPageSort sort) {
        int validatedPage = 0;
        if (page > 0) validatedPage = page - 1;

        Sort pageSort = Sort.by(
                EPageSort.ASCENDING.equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC, Constants.REQUEST_PAGE_SORT_BY);

        return PageRequest.of(validatedPage, size, pageSort);
    }

    private String validationYearMonth(String yearMonth) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentYear = currentDateTime.getYear();
        int currentMonth = currentDateTime.getMonthValue();

        return StringUtils.isNotEmpty(yearMonth) ? yearMonth : String.format(Constants.REQUEST_YEAR_MONTH_PATTERN, currentYear, currentMonth);
    }

    private ArrayList<SearchRankResponse> getContentsFromSearchRankMonth(List<SearchRankMonthResponse> searchRankMonthResponseList, EPageSort sort) {

        ArrayList<SearchRankMonthResponse> targetList = new ArrayList<>(searchRankMonthResponseList);
        if (EPageSort.ASCENDING.equals(sort))
            Collections.reverse(targetList);

        ArrayList<SearchRankResponse> responseContents = new ArrayList<>();
        int rankNum = 0;
        int lastNumberOfSearches = -1;
        for (SearchRankMonthResponse searchRank : targetList) {
            SearchRankResponse rank = new SearchRankResponse();

            if (lastNumberOfSearches != searchRank.getNumberOfSearches()) {
                rankNum++;
                lastNumberOfSearches = searchRank.getNumberOfSearches();
            }

            rank.setRanking(rankNum);
            rank.setKeyWord(searchRank.getKeyWord());
            rank.setNumberOfSearches(searchRank.getNumberOfSearches());
            responseContents.add(rank);
        }

        if (EPageSort.ASCENDING.equals(sort))
            Collections.reverse(responseContents);

        return responseContents;
    }

    private <T> PaginationResponse buildPaginationResponse(Page<T> pageSearchRank, ArrayList<SearchRankResponse> items) {
        PaginationResponse response = new PaginationResponse();
        response.setItemsPerPage(pageSearchRank.getContent().size());

        if (pageSearchRank.getTotalPages() == 0) response.setPage(0);
        else response.setPage(pageSearchRank.getNumber() + 1);

        response.setTotal(pageSearchRank.getTotalElements());
        response.setHasNextPage(pageSearchRank.hasNext());

        response.setItems(items);
        return response;
    }
}
