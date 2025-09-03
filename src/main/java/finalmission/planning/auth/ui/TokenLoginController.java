package finalmission.planning.auth.ui;

import finalmission.planning.auth.application.AuthService;
import finalmission.planning.auth.application.dto.TokenDto;
import finalmission.planning.auth.infra.CookieProvider;
import finalmission.planning.auth.ui.dto.request.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class TokenLoginController {

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    public TokenLoginController(AuthService authService, CookieProvider cookieProvider) {
        this.authService = authService;
        this.cookieProvider = cookieProvider;
    }

    @PostMapping
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        TokenDto tokenDto = authService.createToken(loginRequest);

        Cookie cookie = cookieProvider.createTokenCookie(tokenDto.accessToken());
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }
}
