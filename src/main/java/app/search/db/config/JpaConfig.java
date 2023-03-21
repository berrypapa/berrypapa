package app.search.db.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

/**
 * @brief Jpa 설정 config.
 * @date 2023.03.18
 */
@Configuration
@EntityScan(basePackages = {"app.search.blog.entity"})
public class JpaConfig {
}
