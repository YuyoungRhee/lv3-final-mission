package finalmission.planning.infra.emailSender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties("post-mark")
public class PostMarkProperties {

    private final String serverToken;
}
