package app.search.blog.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchBlogNaverResponse {

    @Getter
    @Setter
    @ToString
    public static class items {
        @Schema(description = "블로그 포스트의 제목. 제목에서 검색어와 일치하는 부분은 <b> 태그로 감싸져 있습니다.")
        private String title;

        @Schema(description = "블로그 포스트의 URL")
        private String link;

        @Schema(description = "블로그 포스트의 내용을 요약한 패시지 정보. 패시지 정보에서 검색어와 일치하는 부분은 <b> 태그로 감싸져 있습니다.")
        private String description;

        @Schema(description = "블로그 포스트가 있는 블로그의 이름")
        private String bloggername;

        @Schema(description = "블로그 포스트가 있는 블로그의 주소")
        private String bloggerlink;

        @Schema(description = "블로그 포스트가 작성된 날짜 (20230318)")
        private String postdate;
    }

    @Schema(description = "검색 결과를 생성한 시간(Sun, 19 Mar 2023 17:15:43 +0900)")
    private String lastBuildDate;

    @Schema(description = "총 검색 결과 개수")
    private int total;

    @Schema(description = "검색 시작 위치(기본값: 1, 최댓값: 1000)")
    private int start;

    @Schema(description = "한 번에 표시할 검색 결과 개수(기본값: 10, 최댓값: 100)")
    private int display;

    @Schema(description = "블로그 디테일 정보")
    private List<items> items;
}
