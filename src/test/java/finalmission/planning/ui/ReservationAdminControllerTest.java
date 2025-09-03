package finalmission.planning.ui;

import static finalmission.planning.TestFixture.DEFAULT_PLAN_DATE;
import static finalmission.planning.TestFixture.DEFAULT_TIME_SLOT_1;
import static finalmission.planning.TestFixture.DEFAULT_TIME_SLOT_2;
import static finalmission.planning.TestFixture.createAdminUser;
import static finalmission.planning.TestFixture.createNormalUser;
import static finalmission.planning.TestFixture.createNormalUserByName;
import static org.assertj.core.api.Assertions.assertThat;

import finalmission.planning.domain.PlanDate;
import finalmission.planning.domain.Reservation;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.domain.TimeSlot;
import finalmission.planning.domain.User;
import finalmission.planning.infra.repository.ReservationRepository;
import finalmission.planning.ui.dto.request.ModifyReservationRequest;
import finalmission.planning.ui.dto.response.ReservationResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class ReservationAdminControllerTest extends IntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("관리자 권한이 없는 유저가 admin 서비스를 사용하려고 할 때 예외 발생")
    @Test
    void forbiddenError_whenNormalUser() {
        // given
        User admin = dbHelper.insertUser(createNormalUser());
        String token = jwtTokenProvider.createToken(admin.getId(), admin.getRole());

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @DisplayName("모든 예약을 조회한다.")
    @Test
    void getAllReservations() {
        // given
        User user1 = dbHelper.insertUser(createNormalUserByName("유저1"));
        User user2 = dbHelper.insertUser(createNormalUserByName("유저2"));
        ReservationSlot reservationSlot1 = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));
        ReservationSlot reservationSlot2 = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_2));
        dbHelper.insertReservation(new Reservation(user1, reservationSlot1));
        dbHelper.insertReservation(new Reservation(user2, reservationSlot2));

        User admin = dbHelper.insertUser(createAdminUser());
        String token = jwtTokenProvider.createToken(admin.getId(), admin.getRole());

        // when
        List<ReservationResponse> responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList(".", ReservationResponse.class);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(ReservationResponse::ownerName)
                .containsExactly("유저1", "유저2");
    }

    @DisplayName("예약 상세 정보 id로 조회")
    @Test
    void getReservationById() {
        // given
        User admin = dbHelper.insertUser(createAdminUser());
        String token = jwtTokenProvider.createToken(admin.getId(), admin.getRole());

        PlanDate planDate = DEFAULT_PLAN_DATE;
        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(planDate, DEFAULT_TIME_SLOT_1));

        Reservation reservation = dbHelper.insertReservation(new Reservation(admin, reservationSlot));

        // when
        ReservationResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationResponse.class);

        // then
        assertThat(response.ownerName()).isEqualTo(admin.getName());
        assertThat(response.reservationSlot().date()).isEqualTo(planDate.getDate());
    }

    @DisplayName("id로 특정 예약 삭제")
    @Test
    void deleteReservation() {
        // given
        User admin = dbHelper.insertUser(createAdminUser());
        String token = jwtTokenProvider.createToken(admin.getId(), admin.getRole());

        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));
        Reservation reservation = dbHelper.insertReservation(new Reservation(admin, reservationSlot));
        Long reservationId = reservation.getId();

        // when
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/admin/reservations/" + reservationId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(reservationRepository.findById(reservationId)).isEmpty();
    }

    @DisplayName("특정 예약을 다른 예약슬롯으로 변경")
    @Test
    void modifyReservation() {
        // given
        User admin = dbHelper.insertUser(createAdminUser());
        String token = jwtTokenProvider.createToken(admin.getId(), admin.getRole());

        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));

        TimeSlot changeTimeSlot = DEFAULT_TIME_SLOT_2;
        ReservationSlot reservationSlotToChange = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, changeTimeSlot)); // timeSlot을 2로 변경

        Reservation reservation = dbHelper.insertReservation(new Reservation(admin, reservationSlot));

        ModifyReservationRequest modifyRequest = new ModifyReservationRequest(reservationSlotToChange.getId());

        // when
        ReservationResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(modifyRequest)
                .when().put("/admin/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationResponse.class);

        // then
        assertThat(response.ownerName()).isEqualTo(admin.getName());
        assertThat(response.reservationSlot().startTime()).isEqualTo(changeTimeSlot.getStartTime());
        assertThat(response.reservationSlot().endTime()).isEqualTo(changeTimeSlot.getEndTime());
    }
}
