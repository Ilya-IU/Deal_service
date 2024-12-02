package com.example.services;

import com.example.Dto.LoanOfferDto;
import com.example.Dto.LoanStatementRequestDto;
import com.example.entity.Client;
import com.example.entity.Credit;
import com.example.entity.Statement;
import com.example.enums.ApplicationStatus;
import com.example.feignClient.CalculateClient;
import com.example.repository.ClientRepository;
import com.example.repository.CreditRepository;
import com.example.repository.StatementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementServiceImplTest {

    @Mock
    private CalculateClient calculateClient;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private StatementServiceImpl statementService;

    @Test
    public void testGetListOffers() {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
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

        List<LoanOfferDto> loanOfferDto = new ArrayList<>();
        loanOfferDto.add(new LoanOfferDto());

        when(calculateClient.getOffers(requestDto)).thenReturn(loanOfferDto);

        statementService.getListOffers(requestDto);

        verify(clientRepository, times(1)).save(any(Client.class));
        verify(creditRepository, times(1)).save(any(Credit.class));
        verify(statementRepository, times(1)).save(any(Statement.class));
    }


}