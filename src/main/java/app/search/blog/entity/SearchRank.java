package app.search.blog.entity;

import app.search.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "search_rank")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class SearchRank extends BaseEntity {
    public SearchRank(String keyWord) {
        this.keyWord = keyWord;
        this.numberOfSearches = 0;
    }

    @Column(name = "key_word")
    private String keyWord;

    @Column(name = "number_of_searches")
    private int numberOfSearches;

    public int addCount() {
        return this.numberOfSearches++;
    }
}
