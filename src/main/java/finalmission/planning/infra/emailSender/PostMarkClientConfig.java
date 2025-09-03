package finalmission.planning.infra.emailSender;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PostMarkProperties.class)
public class PostMarkClientConfig {

    private final PostMarkProperties postMarkProperties;

    @Bean
    public PostMarkEmailClient emailClient() {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://api.postmarkapp.com")
                .defaultHeader("X-Postmark-Server-Token", postMarkProperties.getServerToken())
                .build();

        return new PostMarkEmailClient(restClient);
    }
}
