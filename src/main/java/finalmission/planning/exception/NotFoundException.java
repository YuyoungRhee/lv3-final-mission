package finalmission.planning.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {
    public NotFoundException(String target, String value) {
        super(HttpStatus.NOT_FOUND, "%s를 찾을 수 없습니다, 입력값: %s".formatted(target, value));
    }
}
