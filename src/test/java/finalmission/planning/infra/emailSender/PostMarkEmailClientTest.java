package finalmission.planning.infra.emailSender;

import finalmission.planning.infra.EmailRequest;
import finalmission.planning.ui.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostMarkEmailClientTest extends IntegrationTest {

    @Autowired
    PostMarkEmailClient postMarkEmailClient;

    @Disabled // 실제 API를 호출하는 테스트 - 확인 필요 시 활성화
    @DisplayName("이메일을 전송한다.")
    @Test
    void sendEmail_success() {
        // given
        EmailRequest emailRequest = new EmailRequest("wooung@duksung.ac.kr",
                "wooung@duksung.ac.kr",
                "Hello from Postmark",
                "<strong>Hello</strong> dear Postmark user.",
                "outbound");

        // when
        postMarkEmailClient.sendEmail(emailRequest);

        // then
        // 실제 이메일함 확인
    }

}
