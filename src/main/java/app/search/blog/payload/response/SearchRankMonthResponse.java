package app.search.blog.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

public interface SearchRankMonthResponse {
    @Schema(description = "key_word")
    String getKeyWord();

    @Schema(description = "number_of_searches")
    int getNumberOfSearches();
}
