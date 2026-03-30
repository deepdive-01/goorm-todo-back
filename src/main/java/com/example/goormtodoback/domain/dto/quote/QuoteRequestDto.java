package com.example.goormtodoback.domain.dto.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 사용자 입력 DTO
@Getter
@NoArgsConstructor
public class QuoteRequestDto {

    // 명언을 입력받을 수 있게, 빈 값은 안됨
    @NotBlank(message = "명언 내용은 필수입니다.")
    @JsonProperty("content")
    private String content;

    // 출처를 작성받을 수 있게, 선택적으로
    @JsonProperty("author")
    private String author;
}
