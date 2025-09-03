package finalmission.planning.application;

import finalmission.planning.domain.Reservation;
import finalmission.planning.infra.EmailRequest;
import finalmission.planning.infra.emailSender.PostMarkEmailClient;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final PostMarkEmailClient postMarkEmailClient;

    public void sendEmailForReservation(Reservation reservation) {
        String name = reservation.getOwnerName();
        String date = reservation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startTime = reservation.getTimeSlot().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = reservation.getTimeSlot().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        String messageBody = "%s님이 %s %s - %s 에 예약했습니다.".formatted(name, date, startTime, endTime);

        EmailRequest emailRequest = new EmailRequest("wooung@duksung.ac.kr",
                "wooung@duksung.ac.kr",
                messageBody,
                "dear Postmark user.",
                "outbound");

        postMarkEmailClient.sendEmail(emailRequest);
    }
}
