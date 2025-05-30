package aii.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Exception for forbidden access (HTTP 403).
//This is used when a user is authenticated but does not have sufficient permissions.

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ForbiddenAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ForbiddenAccessException() {
        super();
    }

    public ForbiddenAccessException(String message) {
        super(message);
    }

    public ForbiddenAccessException(Throwable cause) {
        super(cause);
    }

    public ForbiddenAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
