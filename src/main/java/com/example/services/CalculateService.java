package com.example.services;

import com.example.Dto.FinishRegistrationRequestDto;

public interface CalculateService {
    void ScoringCalculateOffer(FinishRegistrationRequestDto requestDto, Long applicationId);
}
