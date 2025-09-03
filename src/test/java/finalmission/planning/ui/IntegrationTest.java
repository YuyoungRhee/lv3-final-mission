package finalmission.planning.ui;

import finalmission.DBCleaner;
import finalmission.planning.DBHelper;
import finalmission.planning.auth.infra.JwtTokenProvider;
import finalmission.planning.domain.User;
import finalmission.planning.domain.UserRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected DBHelper dbHelper;

    @Autowired
    private DBCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void cleanDatabase() {
        dbCleaner.clean();
    }

    protected String saveNormalUserAndCreateToken() {
        User saved = dbHelper.insertUser(new User("토큰용 유저", "user@email.com", "1234", UserRole.NORMAL));
        return jwtTokenProvider.createToken(saved.getId(), saved.getRole());
    }

    protected String saveAdminUserAndCreateToken() {
        User saved = dbHelper.insertUser(new User("토큰용 유저", "user@email.com", "1234", UserRole.ADMIN));
        return jwtTokenProvider.createToken(saved.getId(), saved.getRole());
    }
}
