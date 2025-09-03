package finalmission;

import finalmission.planning.infra.EmailRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public class PostmarkTest {

    @Disabled
    @DisplayName("이메일 보내기 test")
    @Test
    void sendEmail() {

        RestClient emailClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://api.postmarkapp.com")
                .build();

        EmailRequest emailRequest = new EmailRequest("wooung@duksung.ac.kr",
                "wooung@duksung.ac.kr",
                "Hello from Postmark",
                "<strong>Hello</strong> dear Postmark user.",
                "outbound");
        emailClient.post()
                .uri("/email")
                .header("X-Postmark-Server-Token", "e212a00d-8537-47b3-8dd6-7537f613d327")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(emailRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
