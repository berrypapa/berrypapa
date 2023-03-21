package app.search.webclient.service.impl;

import app.search.blog.payload.response.PaginationResponse;
import app.search.blog.payload.response.SearchBlogKakaoResponse;
import app.search.blog.payload.response.SearchBlogNaverResponse;
import app.search.webclient.Constants;
import app.search.webclient.enums.ESearchBlogSortType;
import app.search.webclient.enums.ETargetBlog;
import app.search.webclient.factory.WebClientFactory;
import app.search.webclient.service.WebClientService;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {

    private final WebClientFactory webClientFactory;

    @Override
    public Mono<PaginationResponse> findBlogBySearchWordAndSortType(String query, ESearchBlogSortType sort, int page, int size) {
        log.info("블로그 검색을 시작 합니다.");
        Mono<PaginationResponse> kakaoResponse = findKakaoBlogBySearchWordAndSortType(query, sort, page, size);

        return kakaoResponse.onErrorResume(error -> findNaverBlogBySearchWordAndSortType(query, sort, page, size));
    }

    private WebClient getWebClient(ETargetBlog targetBlog) {
        return webClientFactory.getWebClient(targetBlog);
    }

    private Mono<PaginationResponse> findKakaoBlogBySearchWordAndSortType(String query, ESearchBlogSortType sort, int page, int size) {
        log.info("카카오 블로그를 검색 합니다.");
        WebClient kakaoClient = getWebClient(ETargetBlog.KAKAO);

        return kakaoClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(Constants.QUERY_PARAM_NAME_QUERY, query)
                        .queryParam(Constants.QUERY_PARAM_NAME_SORT, sort.getKakaoValue())
                        .queryParam(Constants.QUERY_PARAM_NAME_PAGE, page)
                        .queryParam(Constants.QUERY_PARAM_NAME_SIZE, size)
                        .build())
                .exchangeToMono(
                        clientResponse -> {
                            String resultMessage = Constants.RESULT_OK;
                            if (HttpStatus.OK.equals(clientResponse.statusCode())) {
                                return clientResponse.bodyToMono(SearchBlogKakaoResponse.class)
                                        .map(result -> convertKakaoResponseToPagination(result, page));
                            } else if (clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()) {
                                resultMessage = String.format("%s %s %s", Constants.ERROR_COMMUNICATION_4XX_5XX, ETargetBlog.KAKAO.name(), clientResponse.statusCode());
                            } else {
                                resultMessage = Constants.CONDITION_RETRY_OK;
                            }
                            return Mono.error(new RuntimeException(resultMessage));
                })
                .timeout(Duration.ofSeconds(2))
                .onErrorMap(ReadTimeoutException.class, error -> {
                    String errorMsg = String.format("An read timeout error occurred in the Kakao blog search. %s", error.getMessage());
                    log.error(errorMsg);
                    throw new ReadTimeoutException(Constants.CONDITION_RETRY_OK);
                })
                .onErrorMap(WriteTimeoutException.class, error -> {
                    String errorMsg = String.format("An write timeout error occurred in the Kakao blog search. %s", error.getMessage());
                    log.error(errorMsg);
                    throw new WriteTimeoutException(errorMsg);
                })
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                         .onRetryExhaustedThrow(
                                 (retrySpec, retrySignal) -> {
                                     log.error(retrySignal.toString());
                                     throw new RuntimeException(Constants.RETRY_EXCEPTION);
                                 }
                         )
                         .doBeforeRetry(
                                 before -> log.info("Search Kakao blog retried at {}, RetrySignal info: {}", LocalTime.now(), before.toString())
                         )
                        .filter(this::checkRetryCondition)
                );
    }

    private Mono<PaginationResponse> findNaverBlogBySearchWordAndSortType(String query, ESearchBlogSortType sort, int page, int size) {
        log.info("네이버 블로그를 검색 합니다.");
        WebClient naverClient = getWebClient(ETargetBlog.NAVER);

        return naverClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(Constants.QUERY_PARAM_NAME_QUERY, query)
                        .queryParam(Constants.QUERY_PARAM_NAME_SORT, sort.getNaverValue())
                        .queryParam(Constants.QUERY_PARAM_NAME_START, page)
                        .queryParam(Constants.QUERY_PARAM_NAME_DISPLAY, size)
                        .build())
                .exchangeToMono(
                        clientResponse -> {
                            String resultMessage = Constants.RESULT_OK;
                            if (HttpStatus.OK.equals(clientResponse.statusCode())) {
                                return clientResponse.bodyToMono(SearchBlogNaverResponse.class)
                                        .map(this::convertNaverResponseToPagination);
                            } else if (clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()) {
                                resultMessage = String.format("%s %s %s", Constants.ERROR_COMMUNICATION_4XX_5XX, ETargetBlog.NAVER.name(), clientResponse.statusCode());
                            } else {
                                resultMessage = Constants.CONDITION_RETRY_OK;
                            }
                            return Mono.error(new RuntimeException(resultMessage));
                })
                .timeout(Duration.ofSeconds(2))
                .onErrorMap(ReadTimeoutException.class, error -> {
                    String errorMsg = String.format("An read timeout error occurred in the Naver blog search. %s", error.getMessage());
                    log.error(errorMsg);
                    throw new ReadTimeoutException(Constants.CONDITION_RETRY_OK);
                })
                .onErrorMap(WriteTimeoutException.class, error -> {
                    String errorMsg = String.format("An write timeout error occurred in the Naver blog search. %s", error.getMessage());
                    log.error(errorMsg);
                    throw new WriteTimeoutException(errorMsg);
                })
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                        .onRetryExhaustedThrow(
                                (retrySpec, retrySignal) -> {
                                    log.error(retrySignal.toString());
                                    throw new RuntimeException(Constants.RETRY_EXCEPTION);
                                }
                        )
                        .doBeforeRetry(
                                before -> log.info("Search Naver blog retried at {}, RetrySignal info: {}", LocalTime.now(), before.toString())
                        )
                        .filter(this::checkRetryCondition)
                );
    }

    private PaginationResponse convertKakaoResponseToPagination(SearchBlogKakaoResponse result, int page) {
        PaginationResponse response = new PaginationResponse();
        response.setTargetBlog((ETargetBlog.KAKAO));

        SearchBlogKakaoResponse.Meta meta = result.getMeta();

        response.setPage(page);
        response.setTotal(meta.getTotalCount());
        response.setItemsPerPage(result.getDocuments().size());
        response.setHasNextPage(!meta.isEnd());
        response.setItems(result.getDocuments());

        return response;
    }

    private PaginationResponse convertNaverResponseToPagination(SearchBlogNaverResponse result) {
        PaginationResponse response = new PaginationResponse();
        response.setTargetBlog((ETargetBlog.NAVER));

        response.setPage(result.getStart());
        response.setTotal(result.getTotal());
        response.setItemsPerPage(result.getDisplay());
        response.setHasNextPage((result.getStart() * result.getDisplay()) < result.getTotal());
        response.setItems(result.getItems());

        return response;
    }

    private boolean checkRetryCondition(Throwable ex) {
        return Constants.CONDITION_RETRY_OK.equals(ex.getMessage());
    }
}
