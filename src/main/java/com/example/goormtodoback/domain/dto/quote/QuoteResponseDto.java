package com.example.goormtodoback.domain.dto.quote;

import com.example.goormtodoback.domain.entity.Quote;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


// 명언 응답 DTO
@Getter
public class QuoteResponseDto {

    // 명언 ID
    @JsonProperty("quote_id")
    private final Long quoteId;

    // 명언
    @JsonProperty("content")
    private final String content;

    // 출처
    @JsonProperty("author")
    private final String author;

    private QuoteResponseDto(Quote quote) {
        this.quoteId = quote.getId();
        this.content = quote.getContent();
        this.author = quote.getAuthor();
    }

    public static QuoteResponseDto from(Quote quote) {
        return new QuoteResponseDto(quote);
    }

}
