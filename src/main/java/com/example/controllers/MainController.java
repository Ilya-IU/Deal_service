package com.example.controllers;

import com.example.Dto.FinishRegistrationRequestDto;
import com.example.Dto.LoanOfferDto;
import com.example.Dto.LoanStatementRequestDto;
import com.example.services.CalculateScoringService;
import com.example.services.StatementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/deal")
@Slf4j
@Tag(name = "Контроллер для расчетов предложений и  формирования сущностей в БД",
        description = "Расчитываются кредитные предложения и сохраняются данные клиента и заявки в БД. ")
public class MainController {
    @Autowired
    private StatementService statementService;

    @Autowired
    private CalculateScoringService calculateScoringService;



    @PostMapping(value = "/statement")
    public List<LoanOfferDto> getOffers(@RequestBody LoanStatementRequestDto requestDto) throws IllegalArgumentException {
        try {
            log.info("Вызывается метод по формированию списка предложений Request: {}", requestDto);
                return statementService.getListOffers(requestDto);
            }
            catch (IllegalArgumentException e){
            log.info("Ошибка прескоринга " + e.getMessage());
                throw new IllegalArgumentException("Ошибка прескоринга "+e.getMessage());
            }

    }


    @PostMapping(value = "/offer")
    public void selectStatusHistory(@RequestBody LoanOfferDto requestDto) {
        log.info("Вызов метода изменения статуса заявки с параметрами Request: {}", requestDto);
        statementService.updateStatusOffer(requestDto);
    }

    @PostMapping(value = "/calculate/{statementId}")
    public void FinishRegistrationAndCredit(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                            @PathVariable Long statementId) {
        log.info("Запущен сервис скоринга с параметрами finishRegistrationRequestDto: {}, statementId = {}",
                finishRegistrationRequestDto, statementId);
        calculateScoringService.ScoringCalculateOffer(finishRegistrationRequestDto, Long.parseLong(String.valueOf(statementId)));
    }

}
