package finalmission.planning.auth.exception;

import finalmission.planning.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }
}
