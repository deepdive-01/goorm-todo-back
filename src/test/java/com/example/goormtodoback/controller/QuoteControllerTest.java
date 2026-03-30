package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.entity.Quote;
import com.example.goormtodoback.domain.dto.quote.QuoteResponseDto;
import com.example.goormtodoback.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuoteService quoteService;

    private QuoteResponseDto quoteResponseDto;

    @BeforeEach
    void setUp() throws Exception {
        Quote quote = Quote.builder()
                .content("오늘 할 일을 내일로 미루지 마라.")
                .author("벤자민 프랭클린")
                .build();
        setId(quote, 1L);
        quoteResponseDto = QuoteResponseDto.from(quote);
    }

    private void setId(Quote quote, Long id) {
        try {
            var field = Quote.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(quote, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("명언 등록 - 성공 (201)")
    void createQuote_success() throws Exception {
        given(quoteService.createQuote(any())).willReturn(quoteResponseDto);

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "content": "오늘 할 일을 내일로 미루지 마라.",
                                    "author": "벤자민 프랭클린"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").value("오늘 할 일을 내일로 미루지 마라."))
                .andExpect(jsonPath("$.data.author").value("벤자민 프랭클린"));
    }

    @Test
    @DisplayName("명언 등록 - content 없으면 400 반환")
    void createQuote_missingContent() throws Exception {
        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "author": "벤자민 프랭클린"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("랜덤 명언 조회 - 성공 (200)")
    void getRandomQuote_success() throws Exception {
        given(quoteService.getRandomQuote()).willReturn(quoteResponseDto);

        mockMvc.perform(get("/api/v1/quotes/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").value("오늘 할 일을 내일로 미루지 마라."))
                .andExpect(jsonPath("$.data.author").value("벤자민 프랭클린"));
    }

    @Test
    @DisplayName("랜덤 명언 조회 - 명언 없으면 404 반환")
    void getRandomQuote_notFound() throws Exception {
        given(quoteService.getRandomQuote())
                .willThrow(new CustomException(ErrorCode.QUOTE_NOT_FOUND));

        mockMvc.perform(get("/api/v1/quotes/random"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("QUOTE_NOT_FOUND"));
    }
}