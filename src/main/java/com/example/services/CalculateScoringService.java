package com.example.services;

import com.example.Dto.FinishRegistrationRequestDto;

public interface CalculateScoringService {
    void ScoringCalculateOffer(FinishRegistrationRequestDto requestDto, Long statementId);
}
