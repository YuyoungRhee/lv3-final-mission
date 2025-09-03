package finalmission.planning.infra.emailSender;

import finalmission.planning.infra.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class PostMarkEmailClient {

    private final RestClient restClient;

    public void sendEmail(EmailRequest emailRequest) {
        restClient.post()
                .uri("/email")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(emailRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
