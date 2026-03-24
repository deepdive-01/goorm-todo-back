package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.response.ApiResponse;
import com.example.goormtodoback.domain.dto.quote.QuoteRequestDto;
import com.example.goormtodoback.domain.dto.quote.QuoteResponseDto;
import com.example.goormtodoback.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponseDto>> createQuote(@Valid @RequestBody QuoteRequestDto dto) {
        QuoteResponseDto response = quoteService.createQuote(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("명언이 등록되었습니다.", response));
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponse<QuoteResponseDto>> getRandomQuote() {
        QuoteResponseDto response = quoteService.getRandomQuote();
        return ResponseEntity
                .ok(ApiResponse.success("랜덤 명언을 조회했습니다.", response));
    }
}
