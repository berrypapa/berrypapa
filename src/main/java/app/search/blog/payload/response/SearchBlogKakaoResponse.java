package app.search.blog.payload.response;

import app.search.webclient.enums.ETargetBlog;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SearchBlogKakaoResponse {
    public SearchBlogKakaoResponse() {
        this.meta = new Meta();
        this.documents = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    public static class Meta {
        @Schema(description = "검색된 문서 수")
        @JsonProperty("total_count")
        private int totalCount; // == total

        @Schema(description = "total_count 중 노출 가능 문서 수")
        @JsonProperty("pageable_count")
        private int pageableCount; // == (start * display)

        @Schema(description = "현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음")
        @JsonProperty("is_end")
        private boolean isEnd; // == (start * display) >= total
    }

    @Getter
    @Setter
    @ToString
    public static class Documents {
        @Schema(description = "블로그 글 제목")
        private String title;

        @Schema(description = "블로그 글 요약")
        private String contents; // == description

        @Schema(description = "블로그 글 URL")
        private String url; // == link

        @Schema(description = "블로그의 이름")
        private String blogname; // == bloggername

        @Schema(description = "검색 시스템에서 추출한 대표 미리보기 이미지 URL, 미리보기 크기 및 화질은 변경될 수 있음")
        private String thumbnail;

        @Schema(description = "블로그 글 작성시간, ISO 8601 [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz]")
        private String datetime; // == postdate
    }

    @Schema(description = "어디에서 검색했는지 정보")
    private ETargetBlog targetBlog;

    @Schema(description = "페이지 및 사이즈 정보")
    private Meta meta;

    @Schema(description = "블로그 디테일 정보")
    private List<Documents> documents;
}
