package com.ansj.demo.controller;

import com.ansj.demo.config.SecurityConfig;
import com.ansj.demo.config.TestSecurityConfig;
import com.ansj.demo.dto.ArticleCommentDto;
import com.ansj.demo.dto.request.ArticleCommentRequest;
import com.ansj.demo.service.ArticleCommentService;
import com.ansj.demo.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("View 컨트롤러 - 댓글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleCommentController.class)
class ArticleCommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private ArticleCommentService articleCommentService;

    public ArticleCommentControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "ansjtest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 댓글 등록 - 정상 호출")
    @Test
    void givenArticleCommentInfo_whenRequesting_thenSavesNewArticleComment() throws Exception {
        // Given
        long articleId = 1L;
        ArticleCommentRequest request = ArticleCommentRequest.of(articleId, "test comment");
        BDDMockito.willDoNothing().given(articleCommentService).saveArticleComment(BDDMockito.any(ArticleCommentDto.class));

        // When
        mvc.perform(
                MockMvcRequestBuilders.post("/comments/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(request))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/articles/" + articleId));

        // Then
        BDDMockito.then(articleCommentService).should().saveArticleComment(BDDMockito.any(ArticleCommentDto.class));

    }

    @WithUserDetails(value = "unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 댓글 삭제 - 정상 호출")
    @Test
    void givenArticleCommentIdToDelete_whenRequesting_thenDeletesArticleComment() throws Exception {
        // Given
        long articleId = 1L;
        long articleCommentId = 1L;
        String userId = "unoTest";
        BDDMockito.willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId, userId);

        // When
        mvc.perform(
                        MockMvcRequestBuilders.post("/comments/" + articleCommentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/articles/" + articleId));

        // Then
        BDDMockito.then(articleCommentService).should().deleteArticleComment(articleCommentId, userId);
    }

    @WithUserDetails(value = "unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 대댓글 등록 - 정상 호출")
    @Test
    void givenArticleCommentInfoWithParentCommentId_whenRequesting_thenSavesNewChildComment() throws Exception {
        // Given
        long articleId = 1L;
        ArticleCommentRequest request = ArticleCommentRequest.of(articleId, 1L, "test comment");
        BDDMockito.willDoNothing().given(articleCommentService).saveArticleComment(BDDMockito.any(ArticleCommentDto.class));

        // When & Then
        mvc.perform(
                MockMvcRequestBuilders.post("/comments/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(request))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/articles/" + articleId));

    }
}
