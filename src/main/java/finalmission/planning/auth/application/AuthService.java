package finalmission.planning.auth.application;

import finalmission.planning.auth.application.dto.TokenDto;
import finalmission.planning.auth.exception.UnauthorizationException;
import finalmission.planning.auth.infra.JwtTokenProvider;
import finalmission.planning.auth.ui.dto.request.LoginRequest;
import finalmission.planning.domain.User;
import finalmission.planning.infra.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public TokenDto createToken(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UnauthorizationException("이메일 또는 비밀번호가 잘못되었습니다."));

        if (!user.matchesPassword(loginRequest.password())) {
            throw new UnauthorizationException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getRole());
        return new TokenDto(accessToken);
    }
}
