package app.search.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
public class ErrorResponse {
    private HttpStatus status;
    private String message;
}
