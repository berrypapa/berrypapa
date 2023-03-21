package app.search.exception;

import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleDefaultException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler({ReadTimeoutException.class, WriteTimeoutException.class})
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ErrorResponse handleTimeoutException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.REQUEST_TIMEOUT);
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }
}
