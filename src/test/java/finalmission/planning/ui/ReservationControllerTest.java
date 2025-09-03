package finalmission.planning.ui;

import static finalmission.planning.TestFixture.DEFAULT_PLAN_DATE;
import static finalmission.planning.TestFixture.DEFAULT_TIME_SLOT_1;
import static finalmission.planning.TestFixture.DEFAULT_TIME_SLOT_2;
import static finalmission.planning.TestFixture.DEFAULT_TIME_SLOT_3;
import static finalmission.planning.TestFixture.createNormalUser;
import static finalmission.planning.TestFixture.createNormalUserByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import finalmission.planning.application.EmailService;
import finalmission.planning.domain.PlanDate;
import finalmission.planning.domain.Reservation;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.domain.TimeSlot;
import finalmission.planning.domain.User;
import finalmission.planning.infra.repository.ReservationRepository;
import finalmission.planning.ui.dto.request.CreateReservationRequest;
import finalmission.planning.ui.dto.request.ModifyReservationRequest;
import finalmission.planning.ui.dto.response.ReservationResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ReservationControllerTest extends IntegrationTest {

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("로그인되어있는 유저가 예약슬롯을 선택하여 예약 등록이 가능하다.")
    @Test
    void createReservation() {
        // given
        User user = dbHelper.insertUser(createNormalUser());
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());

        PlanDate planDate = DEFAULT_PLAN_DATE;
        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(planDate, DEFAULT_TIME_SLOT_1));

        CreateReservationRequest createReservationRequest = new CreateReservationRequest(reservationSlot.getId());

        // when
        ReservationResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(createReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(ReservationResponse.class);

        // then
        assertThat(response.ownerName()).isEqualTo(user.getName());
        assertThat(response.reservationSlot().date()).isEqualTo(planDate.getDate());
        verify(emailService).sendEmailForReservation(any());
    }

    @DisplayName("현재 로그인되어있는 유저의 예약목록을 모두 조회한다.")
    @Test
    void getReservationsByUser() {
        // given
        User user = dbHelper.insertUser(createNormalUserByName("멍구"));
        User other = dbHelper.insertUser(createNormalUserByName("다른 유저"));
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());

        PlanDate planDate = DEFAULT_PLAN_DATE;
        ReservationSlot reservationSlot1 = dbHelper.insertReservationSlot(
                new ReservationSlot(planDate, DEFAULT_TIME_SLOT_1));
        ReservationSlot reservationSlot2 = dbHelper.insertReservationSlot(
                new ReservationSlot(planDate, DEFAULT_TIME_SLOT_2));
        ReservationSlot reservationSlot3 = dbHelper.insertReservationSlot(
                new ReservationSlot(planDate, DEFAULT_TIME_SLOT_3));

        dbHelper.insertReservation(new Reservation(user, reservationSlot1));
        dbHelper.insertReservation(new Reservation(user, reservationSlot2));
        dbHelper.insertReservation(new Reservation(other, reservationSlot3));

        // when
        List<ReservationResponse> responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList(".", ReservationResponse.class);

        // then
        assertThat(responses).hasSize(2);
    }

    @DisplayName("예약 상세 정보 id로 조회")
    @Test
    void getReservationById() {
        // given
        User user = dbHelper.insertUser(createNormalUserByName("멍구"));
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());

        PlanDate planDate = DEFAULT_PLAN_DATE;
        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(planDate, DEFAULT_TIME_SLOT_1));

        Reservation reservation = dbHelper.insertReservation(new Reservation(user, reservationSlot));

        // when
        ReservationResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationResponse.class);

        // then
        assertThat(response.ownerName()).isEqualTo(user.getName());
        assertThat(response.reservationSlot().date()).isEqualTo(planDate.getDate());
    }

    @DisplayName("다른 유저의 예약 상세 정보 조회 시도 시 예외 발생")
    @Test
    void forbiddenError_getReservationById() {
        // given
        User user = dbHelper.insertUser(createNormalUserByName("멍구"));
        User other = dbHelper.insertUser(createNormalUserByName("다른 유저"));
        String token = jwtTokenProvider.createToken(other.getId(), other.getRole()); // 토큰이 다른 유저임

        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));

        Reservation reservation = dbHelper.insertReservation(new Reservation(user, reservationSlot));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @DisplayName("본인의 예약을 삭제 요청")
    @Test
    void deleteReservation() {
        // given
        User user = dbHelper.insertUser(createNormalUser());
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());

        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));

        Reservation reservation = dbHelper.insertReservation(new Reservation(user, reservationSlot));
        Long reservationId = reservation.getId();

        // when
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/reservations/" + reservationId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(reservationRepository.findById(reservationId)).isEmpty();
    }

    @DisplayName("본인의 예약을 다른 예약슬롯으로 변경 요청")
    @Test
    void modifyReservation() {
        // given
        User user = dbHelper.insertUser(createNormalUser());
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());

        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));

        TimeSlot changeTimeSlot = DEFAULT_TIME_SLOT_2;
        ReservationSlot reservationSlotToChange = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, changeTimeSlot)); // timeSlot을 2로 변경

        Reservation reservation = dbHelper.insertReservation(new Reservation(user, reservationSlot));

        ModifyReservationRequest modifyRequest = new ModifyReservationRequest(reservationSlotToChange.getId());

        // when
        ReservationResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(modifyRequest)
                .when().put("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationResponse.class);

        // then
        assertThat(response.ownerName()).isEqualTo(user.getName());
        assertThat(response.reservationSlot().startTime()).isEqualTo(changeTimeSlot.getStartTime());
        assertThat(response.reservationSlot().endTime()).isEqualTo(changeTimeSlot.getEndTime());
    }
}
