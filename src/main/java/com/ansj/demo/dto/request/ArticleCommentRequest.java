package com.ansj.demo.dto.request;

import com.ansj.demo.dto.ArticleCommentDto;
import com.ansj.demo.dto.UserAccountDto;

public record ArticleCommentRequest(Long articleId, String content) {
    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                articleId, userAccountDto, content
        );
    }
}
