package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.entity.Quote;
import com.example.goormtodoback.domain.dto.quote.QuoteRequestDto;
import com.example.goormtodoback.domain.dto.quote.QuoteResponseDto;
import com.example.goormtodoback.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @InjectMocks
    private QuoteService quoteService;

    private Quote quote;

    @BeforeEach
    void setUp() {
        quote = Quote.builder()
                .content("오늘 할 일을 내일로 미루지 마라.")
                .author("벤자민 프랭클린")
                .build();
        setId(quote, 1L);
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
    @DisplayName("명언 등록 - 성공")
    void createQuote_success() {
        // given
        QuoteRequestDto dto = new QuoteRequestDto();
        setField(dto, "content", "오늘 할 일을 내일로 미루지 마라.");
        setField(dto, "author", "벤자민 프랭클린");

        given(quoteRepository.save(any(Quote.class))).willReturn(quote);

        // when
        QuoteResponseDto result = quoteService.createQuote(dto);

        // then
        assertThat(result.getContent()).isEqualTo("오늘 할 일을 내일로 미루지 마라.");
        assertThat(result.getAuthor()).isEqualTo("벤자민 프랭클린");
        verify(quoteRepository).save(any(Quote.class));
    }

    @Test
    @DisplayName("명언 등록 - author 없이 등록 성공")
    void createQuote_withoutAuthor() {
        // given
        Quote quoteWithoutAuthor = Quote.builder()
                .content("오늘 할 일을 내일로 미루지 마라.")
                .author(null)
                .build();
        setId(quoteWithoutAuthor, 2L);

        QuoteRequestDto dto = new QuoteRequestDto();
        setField(dto, "content", "오늘 할 일을 내일로 미루지 마라.");
        setField(dto, "author", null);

        given(quoteRepository.save(any(Quote.class))).willReturn(quoteWithoutAuthor);

        // when
        QuoteResponseDto result = quoteService.createQuote(dto);

        // then
        assertThat(result.getContent()).isEqualTo("오늘 할 일을 내일로 미루지 마라.");
        assertThat(result.getAuthor()).isNull();
    }

    @Test
    @DisplayName("랜덤 명언 조회 - 성공")
    void getRandomQuote_success() {
        // given
        given(quoteRepository.findRandomQuote()).willReturn(Optional.of(quote));

        // when
        QuoteResponseDto result = quoteService.getRandomQuote();

        // then
        assertThat(result.getQuoteId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("오늘 할 일을 내일로 미루지 마라.");
        assertThat(result.getAuthor()).isEqualTo("벤자민 프랭클린");
    }

    @Test
    @DisplayName("랜덤 명언 조회 - 명언이 없으면 QUOTE_NOT_FOUND 예외")
    void getRandomQuote_notFound() {
        // given
        given(quoteRepository.findRandomQuote()).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> quoteService.getRandomQuote())
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.QUOTE_NOT_FOUND);
                });
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}