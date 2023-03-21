package app.search.webclient.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ESearchBlogSortType {
    ACCURACY("accuracy", "sim"),    // KAKAO : 정확도순 (기본값), NAVER : 정확도순으로 내림차순 정렬(기본값)
    RECENCY("recency", "date");     // KAKAO : 최신순, NAVER : 날짜순으로 내림차순 정렬

    private final String kakaoValue;
    private final String naverValue;
}
