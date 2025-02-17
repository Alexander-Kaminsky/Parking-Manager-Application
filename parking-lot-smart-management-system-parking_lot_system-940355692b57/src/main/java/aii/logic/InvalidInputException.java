package aii.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//Exception for invalid input or bad request (HTTP 400).
//This is used when the input provided by the user is invalid or malformed.

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidInputException() {
        super();
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
