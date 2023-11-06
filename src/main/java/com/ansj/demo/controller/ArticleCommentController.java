package com.ansj.demo.controller;

import com.ansj.demo.dto.UserAccountDto;
import com.ansj.demo.dto.request.ArticleCommentRequest;
import com.ansj.demo.dto.request.ArticleRequest;
import com.ansj.demo.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleCommentService.saveArticleComment(
                articleCommentRequest.toDto(
                        UserAccountDto.of("ansj", "ansj", "ansj@mail.com", "ansj", "ansj")
                ));
        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping ("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId) {
        articleCommentService.deleteArticleComment(commentId);
        return "redirect:/articles/" + articleId;
    }

}
