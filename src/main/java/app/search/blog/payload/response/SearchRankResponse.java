package app.search.blog.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class SearchRankResponse {
    public SearchRankResponse() {
        this.ranking = 0;
        this.keyWord = "";
        this.numberOfSearches = 0;
    }

    @Schema(description = "순위")
    private int ranking;

    @Schema(description = "인기 검색어")
    private String keyWord;

    @Schema(description = "누적 검색 횟수")
    private int numberOfSearches;
}
