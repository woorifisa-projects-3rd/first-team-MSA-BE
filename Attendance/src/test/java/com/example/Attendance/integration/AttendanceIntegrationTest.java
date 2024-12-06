package com.example.Attendance.integration;

import com.example.Attendance.controller.CommuteController;
import com.example.Attendance.dto.CommuteByPresidentRequest;
import com.example.Attendance.dto.CommuteDailyResponse;
import com.example.Attendance.dto.CommuteMonthlyResponse;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
public class AttendanceIntegrationTest {

    @Autowired
    private CommuteController commuteController;

    @Test
    @DisplayName("월별 출퇴근 기록 조회 성공")
    public void getMonthlyCommuteList_success() {
        // given
        int storeId = 1;
        int year = 2024;
        int month = 12;

        // when
        ResponseEntity<List<CommuteMonthlyResponse>> response =
                commuteController.getMonthlyCommuteList(storeId, year, month);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("일별 출퇴근 기록 조회 성공")
    public void getDailyCommuteList_success() {

        int storeId = 1;
        LocalDate commuteDate = LocalDate.of(2024, 12, 3);

        ResponseEntity<List<CommuteDailyResponse>> response =
                commuteController.getDailyCommuteList(storeId, commuteDate);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("사장님의 직원 출퇴근 기록 추가 성공")
    public void addDailyCommuteByPresident_success() {
        // given
        int seId = 1;
//        CommuteByPresidentRequest request = CommuteByPresidentRequest.builder()
//                .startTime(LocalDateTime.of(2024, 12, 5, 9, 0))
//                .endTime(LocalDateTime.of(2024, 12, 5, 18, 0))
//                .commuteDate(LocalDate.of(2024, 12, 5))
//                .build();

        // when
//        ResponseEntity<String> response =
//                commuteController.addDailyCommuteByPresident(seId, request);

        // then
//        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Assertions.assertThat(response.getBody())
//                .isEqualTo("직원 출 퇴근 사장님이 추가 성공");
    }

    @Test
    @DisplayName("사장님의 직원 출퇴근 기록 수정 성공")
    public void updateDailyCommuteByPresident_success() {
        // given
        int commuteId = 1;
//        CommuteByPresidentRequest request = CommuteByPresidentRequest.builder()
//                .startTime(LocalDateTime.of(2024, 12, 5, 9, 30))
//                .endTime(LocalDateTime.of(2024, 12, 5, 18, 30))
//                .commuteDate(LocalDate.of(2024, 12, 5))
//                .build();
//
//        // when
//        ResponseEntity<String> response =
//                commuteController.updateDailyCommuteByPresident(commuteId, request);
//
//        // then
//        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Assertions.assertThat(response.getBody())
//                .isEqualTo("직원 출 퇴근 사장님이 수정 성공");
    }

    @Test
    @DisplayName("출퇴근 기록 삭제 성공")
    public void deleteDailyCommuteByPresident_success() {
        // given
        int commuteId = 1;

        // when
        ResponseEntity<String> response =
                commuteController.deleteDailyCommuteByPresident(commuteId);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .isEqualTo("출 퇴근 기록 삭제 성공");
    }


}
