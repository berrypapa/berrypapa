package app.search.webclient.factory;

import app.search.webclient.Constants;
import app.search.webclient.enums.ETargetBlog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class WebClientFactory {

    private final WebClient webClient;

    @Value("${kakao.search.blog.endpoint}")
    private String kakaoSearchBlogEndpoint;

    @Value("${kakao.search.blog.api.key}")
    private String kakaoRestApiKey;

    @Value("${naver.search.blog.endpoint}")
    private String naverSearchBlogEndpoint;

    @Value("${naver.search.blog.client.id}")
    private String naverClientId;

    @Value("${naver.search.blog.client.secret}")
    private String naverClientSecret;

    public WebClient getWebClient(ETargetBlog targetBlog) {
        WebClient response = null;
        switch(targetBlog) {
            default -> throw new RuntimeException(String.format("target blog 값이 잘 못 되었습니다. : %s", targetBlog));
            case KAKAO -> response = kakao();
            case NAVER ->  response = naver();
        }
        return response;
    }

    protected WebClient kakao() {
        return webClient.mutate()
                .baseUrl(kakaoSearchBlogEndpoint)
                .defaultHeader(HttpHeaders.AUTHORIZATION, Constants.KAKAO_HEADER_VALUE_PREFIX + kakaoRestApiKey)
                .build();
    }

    protected WebClient naver() {
        return webClient.mutate()
                .baseUrl(naverSearchBlogEndpoint)
                .defaultHeader(Constants.NAVER_HEADER_KEY_CLIENT_ID, naverClientId)
                .defaultHeader(Constants.NAVER_HEADER_KEY_CLIENT_SECRET, naverClientSecret)
                .build();
    }
}
