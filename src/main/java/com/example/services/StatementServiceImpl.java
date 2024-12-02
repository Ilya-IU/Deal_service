package com.example.services;

import com.example.Dto.LoanOfferDto;
import com.example.Dto.LoanStatementRequestDto;
import com.example.Dto.Passport;
import com.example.entity.Client;
import com.example.entity.Credit;
import com.example.entity.Statement;
import com.example.feignClient.CalculateClient;
import com.example.Dto.StatementStatusHistoryDto;
import com.example.repository.ClientRepository;
import com.example.repository.CreditRepository;
import com.example.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.ApplicationStatus.PREAPPROVAL;
import static com.example.enums.ChangeType.AUTOMATIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {

    private final CalculateClient calculateClient;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final ClientRepository clientRepository;


    @Override
    public List<LoanOfferDto> getListOffers(LoanStatementRequestDto requestDto) {
        log.info("Формирование списка 4-х предложений с requestDto={}", requestDto);
        List<LoanOfferDto> loanOfferDto = calculateClient.getOffers(requestDto);
        log.info("Список сформирован loanOfferDto={}", loanOfferDto);
        Client client = createClientEntity(requestDto);
        clientRepository.save(client);
        log.info("Сущность Client создана и сохранена");
        Credit credit = createCreditEntity(requestDto);
        creditRepository.save(credit);
        log.info("Сущность Credit создана и сохранена");
        Statement statement = new Statement();
        statement.setClient(client);
        statement.setCredit(credit);
        log.info("Данные заявки заполнены из Credit'a  и Client'a");
        statementRepository.save(statement);
        log.info("Заявка сохранена, возврат loanOfferDto={}", loanOfferDto);
        return loanOfferDto;
    }

     Credit createCreditEntity(LoanStatementRequestDto requestDto) {

        Credit credit = new Credit();
        credit.setAmount(requestDto.getAmount());
        credit.setTerm(requestDto.getTerm());
         log.info("Сущность Credit создана");
        return credit;
    }

     Client createClientEntity(LoanStatementRequestDto requestDto) {

        Client client = new Client();
        client.setFirstName(requestDto.getFirstName());
        client.setLastName(requestDto.getLastName());
        client.setMiddleName(requestDto.getMiddleName());
        client.setBirthdate(requestDto.getBirthdate());
        client.setEmail(requestDto.getEmail());

        Passport passport = new Passport();
        passport.setNumber(requestDto.getPassportNumber());
        passport.setSeries(requestDto.getPassportSeries());
        log.info("Паспорт заполнен");

        client.setPassport(passport);
         log.info("Сущность Client создана");

        return client;
    }

    @Override
    public void updateStatusOffer(LoanOfferDto requestDto) {
        Long id  = requestDto.getStatementId();
        log.info("Updating status of {}", id);
        Statement statement = statementRepository.findById(id).orElse(null);
        log.info("Statement found = {}", statement);
        statement.setStatus(PREAPPROVAL);

        List<StatementStatusHistoryDto> offerStatus = new ArrayList<>();
        offerStatus.add(new StatementStatusHistoryDto(PREAPPROVAL, LocalDate.now(), AUTOMATIC));;
        log.info("Статус заявки обновлен, offerStatus = {}", offerStatus);
        statement.setStatementStatusHistoryDto(offerStatus);
        statement.setAppliedOffer(requestDto);

        statementRepository.save(statement);
        log.info("Зявка заполнена и сохранена");
    }
}
