package com.example.services;

import com.example.Dto.FinishRegistrationRequestDto;

import com.example.Dto.ScoringDataDto;
import com.example.Dto.StatementStatusHistoryDto;
import com.example.entity.Client;
import com.example.entity.Credit;
import com.example.entity.Statement;
import com.example.feignClient.CalculateClient;
import com.example.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.ApplicationStatus.CC_APPROVED;
import static com.example.enums.ChangeType.AUTOMATIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateScoringServiceImpl implements CalculateScoringService {
    @Autowired
    private StatementRepository statementRepository;
    @Autowired
    private CalculateClient calculateClient;

    @Override
    public void ScoringCalculateOffer(FinishRegistrationRequestDto requestDto, Long statementId) {
        log.info("Поиск заявки по id, с параметрами FinishRegistrationRequestDto = {}, statementid = {}", requestDto, statementId);
        Statement statement = statementRepository.findById(statementId).orElse(null);
        log.info("Заявка найдена statement = {}", statement);
        Client client = statement.getClient();
        log.info("Клинет найден statement = {}", client);
        Credit credit = statement.getCredit();
        log.info("Кредит найден statement = {}", credit);
        ScoringDataDto scoringDataDto = createScoringDataDto(requestDto, statement, client, credit);
        log.info("Запуск сервиса Calculate для расчета параметров скоринга scoringDataDto = {}", scoringDataDto);
        calculateClient.calculateCreditParams(scoringDataDto);

    }


    private ScoringDataDto createScoringDataDto(FinishRegistrationRequestDto requestDto,
                                                Statement statement, Client client, Credit credit) {
        log.info("Наполнение scoringDataDto");
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        scoringDataDto.setAmount(credit.getAmount());
        scoringDataDto.setTerm(credit.getTerm());

        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setBirthdate(client.getBirthdate());
        scoringDataDto.setPassportNumber(client.getPassport().getNumber());
        scoringDataDto.setPassportSeries(client.getPassport().getSeries());

        //dto->
        scoringDataDto.setGender(requestDto.getGender());
        scoringDataDto.setMaritalStatus(requestDto.getMaritalStatus());
        scoringDataDto.setDependentAmount(requestDto.getDependentAmount());
        scoringDataDto.setPassportIssueBranch(requestDto.getPassportIssueBranch());
        scoringDataDto.setPassportIssueDate(requestDto.getPassportIssueDate());
        scoringDataDto.setEmployment(requestDto.getEmployment());
        scoringDataDto.setAccountNumber(requestDto.getAccountNumber());
        //бд->
        scoringDataDto.setIsInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnabled());
        scoringDataDto.setIsSalaryClient(statement.getAppliedOffer().getIsSalaryClient());
        log.info("scoringDataDto заполнено параметрами ={}", scoringDataDto);
        statement.setStatus(CC_APPROVED);
        List<StatementStatusHistoryDto> historyStatuses = statement.getStatementStatusHistoryDto();
        historyStatuses.add(new StatementStatusHistoryDto( CC_APPROVED, LocalDate.now(), AUTOMATIC));
        log.info("История статусов заявки в бд historyStatuses = {}", historyStatuses);
        statement.setStatementStatusHistoryDto(historyStatuses);
        log.info("Обновление статуса заявки в БД");

        statementRepository.save(statement);
        log.info("Сохранение заявки в бд/возвращение scoringDataDto");
        return scoringDataDto;
    }
}
