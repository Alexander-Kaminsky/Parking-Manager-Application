package aii.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Exception for unauthorized access (HTTP 401).
//This is used when a user is not authenticated.

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException() {
        super();
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(Throwable cause) {
        super(cause);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
