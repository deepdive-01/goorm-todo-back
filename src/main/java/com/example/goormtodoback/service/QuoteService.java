package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.entity.Quote;
import com.example.goormtodoback.domain.dto.quote.QuoteRequestDto;
import com.example.goormtodoback.domain.dto.quote.QuoteResponseDto;
import com.example.goormtodoback.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;


    // 명언 응답 DTO를 가지는 createQuote 생성
    // builder를 통해 RequestDTO로 저장
    @Transactional
    public QuoteResponseDto createQuote(QuoteRequestDto dto) {
        Quote quote = Quote.builder()
                .content(dto.getContent())
                .author(dto.getAuthor())
                .build();
        return QuoteResponseDto.from(quoteRepository.save(quote));
    }

    @Transactional
    public QuoteResponseDto getRandomQuote() {
        Quote quote = quoteRepository.findRandomQuote()
                .orElseThrow(() -> new CustomException(ErrorCode.QUOTE_NOT_FOUND));

        return QuoteResponseDto.from(quote);
    }


}
