package app.search.blog.payload.response;

import app.search.webclient.enums.ETargetBlog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class PaginationResponse {
    public PaginationResponse() {
        this.itemsPerPage = 0;
        this.page = 0;
        this.total = 0;
        this.hasNextPage = false;
        this.items = new ArrayList<>();
    }

    @Schema(description = "어디에서 검색했는지 정보")
    private ETargetBlog targetBlog;

    @Schema(description = "현재 페이지의 아이템 개수")
    private int itemsPerPage; // 리스트 사이즈

    @Schema(description = "현재 페이지 번호")
    private int page;

    @Schema(description = "총 아이템 개수")
    private long total; // totalCount, total

    @Schema(description = "다음 페이지 여부")
    private boolean hasNextPage; // isEnd, (start * display) < total

    @Schema(description = "데이터")
    private List<?> items;
}
