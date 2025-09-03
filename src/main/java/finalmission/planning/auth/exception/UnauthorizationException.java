package finalmission.planning.auth.exception;

import finalmission.planning.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizationException extends CustomException {
    public UnauthorizationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
