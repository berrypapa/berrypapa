package app.search.webclient.service;

import app.search.blog.config.WebConfig;
import app.search.blog.payload.response.PaginationResponse;
import app.search.webclient.config.WebClientConfig;
import app.search.webclient.enums.ESearchBlogSortType;
import app.search.webclient.enums.ETargetBlog;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class WebClientServiceTest {

    @Autowired
    private WebClientService webClientService;

    @Test
    @DisplayName("카카오 블로그 검색이 정상 작동하여 카카오에서 결과를 받아옴.")
    void findBlogBySearchWordAndSortType() {
        // Given
        String keyWord = "스프링부트";
        ESearchBlogSortType sort = ESearchBlogSortType.RECENCY;
        int page = 1;
        int size = 10;

        // When
        Mono<PaginationResponse> responseMono = webClientService.findBlogBySearchWordAndSortType(keyWord, sort, page, size);
        PaginationResponse response = responseMono.block();
        log.info("Response : {}", response.toString());

        // Then
        assertEquals(ETargetBlog.KAKAO, response.getTargetBlog());
        assertEquals(page, response.getPage());
        assertTrue(response.getItemsPerPage() <= size);
    }

    @SpringBootTest
    @Slf4j
    @Transactional
    @TestPropertySource(properties = {"kakao.search.blog.endpoint=http://localhost"})
    static class WebClientServiceImplFailKakaoTest{
        @Autowired
        private WebClientService webClientService;

        @Test
        @DisplayName("카카오 블로그 검색이 막혀 네이버에서 결과를 받아옴.")
        void findBlogBySearchWordAndSortType() {
            // Given
            String keyWord = "스프링부트";
            ESearchBlogSortType sort = ESearchBlogSortType.RECENCY;
            int page = 1;
            int size = 10;

            // When
            Mono<PaginationResponse> responseMono = webClientService.findBlogBySearchWordAndSortType(keyWord, sort, page, size);
            PaginationResponse response = responseMono.block();
            log.info("Response : {}", response.toString());

            // Then
            assertEquals(ETargetBlog.NAVER, response.getTargetBlog());
            assertEquals(page, response.getPage());
            assertTrue(response.getItemsPerPage() <= size);
        }
    }

    @SpringBootTest
    @Slf4j
    @Transactional
    @TestPropertySource(properties = {
            "kakao.search.blog.endpoint=https://dapi.unknown.com/v2/search/blog",
            "naver.search.blog.endpoint=https://openapi.unknown.com/v1/search/blog.json"
    })
    static class WebClientServiceImplFailNaverTest{
        @Autowired
        private WebClientService webClientService;

        @Test
        @DisplayName("카카오, 네이버 검색이 정상 작동하지 않아 결과를 받아오지 못함.")
        void findBlogBySearchWordAndSortType() {
            // Given
            String keyWord = "스프링부트";
            ESearchBlogSortType sort = ESearchBlogSortType.RECENCY;
            int page = 1;
            int size = 10;

            // Then
            assertThrows(RuntimeException.class, () -> {
                Mono<PaginationResponse> responseMono = webClientService.findBlogBySearchWordAndSortType(keyWord, sort, page, size);
                PaginationResponse response = responseMono.block();
                log.info("Response : {}", response.toString());
            });
        }
    }
}