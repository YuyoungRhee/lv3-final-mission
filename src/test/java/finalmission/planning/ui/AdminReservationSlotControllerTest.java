package finalmission.planning.ui;

import static finalmission.planning.TestFixture.DEFAULT_PLAN_DATE;
import static finalmission.planning.TestFixture.DEFAULT_TIME_SLOT_1;
import static org.assertj.core.api.Assertions.assertThat;

import finalmission.planning.domain.PlanDate;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.domain.TimeSlot;
import finalmission.planning.infra.repository.ReservationSlotRepository;
import finalmission.planning.ui.dto.request.CreateReservationSlotRequest;
import finalmission.planning.ui.dto.request.ModifyReservationSlotRequest;
import finalmission.planning.ui.dto.response.ReservationSlotResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminReservationSlotControllerTest extends IntegrationTest {

    @Autowired
    ReservationSlotRepository reservationSlotRepository;

    @DisplayName("예약 슬롯 추가")
    @Test
    void createReservationSlot() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(12, 0);
        CreateReservationSlotRequest request = new CreateReservationSlotRequest(
                date, startTime, endTime);

        String token = saveAdminUserAndCreateToken();

        // when
        ReservationSlotResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/reservationSlots")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(ReservationSlotResponse.class);

        // then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.date()).isEqualTo(date);
            assertThat(response.startTime()).isEqualTo(startTime);
            assertThat(response.endTime()).isEqualTo(endTime);
        });
    }

    @DisplayName("모든 예약 슬롯 조회")
    @Test
    void getAllReservationSlots() {
        // given
        dbHelper.insertReservationSlot(new ReservationSlot(
                new PlanDate(LocalDate.of(2025, 6, 1)),
                DEFAULT_TIME_SLOT_1
        ));
        dbHelper.insertReservationSlot(new ReservationSlot(
                new PlanDate(LocalDate.of(2025, 6, 2)),
                DEFAULT_TIME_SLOT_1
        ));

        String token = saveAdminUserAndCreateToken();

        // when
        List<ReservationSlotResponse> responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservationSlots")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList(".", ReservationSlotResponse.class);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(ReservationSlotResponse::date)
                .containsExactly(
                        LocalDate.of(2025, 6, 1),
                        LocalDate.of(2025, 6, 2)
                );

    }

    @DisplayName("id로 특정 예약 슬롯 조회")
    @Test
    void getReservationSlotById() {
        // given
        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(new ReservationSlot(
                new PlanDate(LocalDate.of(2025, 6, 1)),
                new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
        ));

        String token = saveAdminUserAndCreateToken();

        // when
        ReservationSlotResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservationSlots/" + reservationSlot.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationSlotResponse.class);

        // then
        assertThat(response.date()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(response.startTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(response.endTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @DisplayName("예약 슬롯 수정")
    @Test
    void modifyReservationSlot() {
        // given
        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(new ReservationSlot(
                new PlanDate(LocalDate.of(2025, 6, 1)),
                new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
        ));

        ModifyReservationSlotRequest modifyRequest = new ModifyReservationSlotRequest(
                LocalDate.of(2025, 7, 1),
                LocalTime.of(10, 0), LocalTime.of(12, 0)
        );

        String token = saveAdminUserAndCreateToken();

        // when
        ReservationSlotResponse response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(modifyRequest)
                .when().put("/admin/reservationSlots/" + reservationSlot.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationSlotResponse.class);

        // then
        assertThat(response.date()).isEqualTo(LocalDate.of(2025, 7, 1)); // 변경됨
        assertThat(response.startTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(response.endTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @DisplayName("id로 특정 예약 슬롯 삭제")
    @Test
    void deleteReservationSlotById() {
        // given
        ReservationSlot reservationSlot = dbHelper.insertReservationSlot(
                new ReservationSlot(DEFAULT_PLAN_DATE, DEFAULT_TIME_SLOT_1));
        Long reservationSlotId = reservationSlot.getId();

        String token = saveAdminUserAndCreateToken();

        // when
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/admin/reservationSlots/" + reservationSlotId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(reservationSlotRepository.findById(reservationSlotId)).isEmpty();
    }
}
