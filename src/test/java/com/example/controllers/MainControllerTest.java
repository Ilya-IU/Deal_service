package com.example.controllers;

import com.example.Dto.*;
import com.example.enums.EmploymentStatus;
import com.example.enums.Gender;
import com.example.enums.MaritalStatus;
import com.example.enums.Position;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private MainController mainController;


    @Test
    void getOffers_ShouldReturnValid() throws Exception {

        LoanStatementRequestDto request =
                LoanStatementRequestDto.builder()
                        .amount(BigDecimal.valueOf(1_000_000))
                        .term(10)
                        .firstName("Vasiliy")
                        .lastName("Vasilev")
                        .middleName("Vasilevich")
                        .email("VVVasilev@gmail.ru")
                        .birthdate(LocalDate.of(2000, 1, 1))
                        .passportSeries("1234")
                        .passportNumber("123456")
                        .build();
        String requestJson = objectMapper.writeValueAsString(request);
        var response = this.mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andReturn();

        Assertions.assertEquals(200, response.getResponse().getStatus());


    }

    @Test
    void getOffers_ShouldReturnNotValid() throws Exception {
        String requestJson = objectMapper.writeValueAsString("");
        var response = this.mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andReturn();
        Assertions.assertEquals(400, response.getResponse().getStatus());


    }

    @Test
    public void selectStatusHistory_ShouldReturnValid() throws Exception {
        LoanOfferDto request = LoanOfferDto.builder()
                .statementId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(120000))
                .term(10)
                .monthlyPayment(BigDecimal.valueOf(12000))
                .rate(BigDecimal.valueOf(20))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
        String requestJson = objectMapper.writeValueAsString(request);
        var response = mockMvc.perform(post("/deal/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andReturn();
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    public void selectStatusHistory_ShouldReturnNotValid() throws Exception {

        String requestJson = objectMapper.writeValueAsString("");
        var response = mockMvc.perform(post("/deal/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andReturn();
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    public void FinishRegistrationAndCredit_ShouldReturnValid() throws Exception {
        ScoringDataDto request = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(100_000))
                .term(10)
                .firstName("Vasya")
                .middleName("Vasilevich")
                .lastName("Vasilev")
                .gender(Gender.MAN)
                .birthdate(LocalDate.of(2000, 1, 1))
                .passportSeries("123456")
                .passportNumber("123456")
                .passportIssueDate(LocalDate.of(2023, 1, 1))
                .passportIssueBranch("gdsfgfd")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(100_000)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                        .employerINN("123456789")
                        .salary(BigDecimal.valueOf(10_000))
                        .workExperienceCurrent(5)
                        .workExperienceTotal(25)
                        .position(Position.MIDDLE_MANAGER)
                        .build())
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
        String requestJson = objectMapper.writeValueAsString(request);
        var response = mockMvc.perform(post("/deal/calculate/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andReturn();
        Assertions.assertEquals(200, response.getResponse().getStatus());

    }

    @Test
    public void FinishRegistrationAndCredit_ShouldReturnNotValid() throws Exception {
        String requestJson = objectMapper.writeValueAsString("");
        var response = mockMvc.perform(post("/deal/calculate/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andReturn();
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }
}


