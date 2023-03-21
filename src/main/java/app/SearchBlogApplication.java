package app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * @brief App entry point.
 * @date 2023.03.18
 */
@SpringBootApplication
public class SearchBlogApplication {

    @PostConstruct
    public void started() {
        // timezone Asia/Seoul 셋팅
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SearchBlogApplication.class, args);
    }

}
