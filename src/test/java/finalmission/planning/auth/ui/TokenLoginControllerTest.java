package finalmission.planning.auth.ui;

import static org.junit.jupiter.api.Assertions.*;

import finalmission.planning.auth.ui.dto.request.LoginRequest;
import finalmission.planning.domain.User;
import finalmission.planning.domain.UserRole;
import finalmission.planning.infra.repository.UserRepository;
import finalmission.planning.ui.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class TokenLoginControllerTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @DisplayName("로그인 요청 시 토큰이 발급되어 쿠키에 저장")
    @Test
    void login() {
        // given
        userRepository.save(new User("멍구", "test@email.com", "password", UserRole.NORMAL));
        LoginRequest loginRequest = new LoginRequest("test@email.com", "password");

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(HttpStatus.OK. value())
                .header("Set-Cookie", Matchers.containsString("token="));
    }
}
