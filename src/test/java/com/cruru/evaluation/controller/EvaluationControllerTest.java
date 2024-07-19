package com.cruru.evaluation.controller;

import com.cruru.applicant.domain.Applicant;
import com.cruru.applicant.domain.repository.ApplicantRepository;
import com.cruru.evaluation.controller.dto.EvaluationCreateRequest;
import com.cruru.process.domain.Process;
import com.cruru.process.domain.repository.ProcessRepository;
import com.cruru.util.ControllerTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

@DisplayName("평가 컨트롤러 테스트")
class EvaluationControllerTest extends ControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    private Process process;

    private Applicant applicant;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        process = processRepository.save(new Process(0, "서류", "서류", null));

        applicant = applicantRepository.save(
                new Applicant("초코칩", "dev.chocochip@gmail.com", "01012345678", process, false)
        );
    }

    @DisplayName("평가 생성 성공 시, 201을 응답한다.")
    @Test
    void create() {
        // given
        int score = 4;
        String content = "서류가 인상적입니다.";
        String url = String.format("/v1/evaluations?process_id=%d&applicant_id=%d", process.getId(), applicant.getId());
        EvaluationCreateRequest request = new EvaluationCreateRequest(score, content);

        // when&then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(url)
                .then().log().all().statusCode(201);
    }

    @DisplayName("지원자가 존재하지 않을 경우, 404를 응답한다.")
    @Test
    void create_applicantNotFound() {
        // given
        int score = 4;
        String content = "서류가 인상적입니다.";
        long invalidApplicantId = -1;
        String url = String.format(
                "/v1/evaluations?process_id=%d&applicant_id=%d",
                process.getId(),
                invalidApplicantId
        );
        EvaluationCreateRequest request = new EvaluationCreateRequest(score, content);

        // when&then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(url)
                .then().log().all().statusCode(404);
    }

    @DisplayName("프로세스가 존재하지 않을 경우, 404를 응답한다.")
    @Test
    void create_processNotFound() {
        // given
        int score = 4;
        String content = "서류가 인상적입니다.";
        long invalidProcessId = -1;
        String url = String.format(
                "/v1/evaluations?process_id=%d&applicant_id=%d",
                invalidProcessId,
                applicant.getId()
        );
        EvaluationCreateRequest request = new EvaluationCreateRequest(score, content);

        // when&then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(url)
                .then().log().all().statusCode(404);
    }

    @DisplayName("평가 조회에 성공할 경우, 200을 응답한다.")
    @Test
    void read() {
        // given
        String url = String.format("/v1/evaluations?process_id=%d&applicant_id=%d", process.getId(), applicant.getId());

        // when&then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get(url)
                .then().log().all().statusCode(200);
    }
}
