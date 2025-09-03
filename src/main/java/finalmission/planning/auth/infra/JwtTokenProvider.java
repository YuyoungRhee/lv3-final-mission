package finalmission.planning.auth.infra;

import finalmission.planning.auth.exception.UnauthorizationException;
import finalmission.planning.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final Key secretkey;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${security.jwt.token.secret-key}") String secretKey,
            @Value("${security.jwt.token.expire-length}") long validityInMilliseconds
    ) {
        this.secretkey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(Long id, UserRole role) {
        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(id));
        claims.put("role", role.name());
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtPayload getPayLoad(String token) {
        Long memberId = getMemberId(token);
        UserRole role = getRole(token);
        return new JwtPayload(memberId, role);
    }

    private Long getMemberId(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            if (subject == null) {
                throw new UnauthorizationException("유효하지 않은 토큰입니다.");
            }

            return Long.parseLong(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizationException("유효하지 않은 토큰입니다.");
        }
    }

    private UserRole getRole(String token) {
        try {
            String rawRole = Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);

            if (rawRole == null) {
                throw new UnauthorizationException("유효하지 않은 토큰입니다.");
            }

            return UserRole.valueOf(rawRole);

        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizationException("유효하지 않은 토큰입니다.");
        }
    }

    public void validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token);

            if(claims.getBody().getExpiration().before(new Date())) {
                throw new UnauthorizationException("유효하지 않은 토큰입니다.");
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizationException("유효하지 않은 토큰입니다.");
        }
    }
}
