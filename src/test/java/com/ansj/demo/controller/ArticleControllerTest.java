package com.ansj.demo.controller;

import com.ansj.demo.config.SecurityConfig;
import com.ansj.demo.dto.ArticleWithCommentsDto;
import com.ansj.demo.dto.UserAccountDto;
import com.ansj.demo.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("view 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private final MockMvc mvc;

    @MockBean private ArticleService articleService;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

//    @Disabled("구현 중")
    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothiing_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {
        // Given
        BDDMockito.given(articleService.searchArticles(
                BDDMockito.eq(null),
                BDDMockito.eq(null),
                BDDMockito.any(Pageable.class)
        )).willReturn(Page.empty());

        // When
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"));

        // Then
        BDDMockito.then(articleService).should().searchArticles(BDDMockito.eq(null),
                BDDMockito.eq(null),
                BDDMockito.any(Pageable.class)
        );
    }

//    @Disabled("구현 중")
    @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void givenNothiing_whenRequestingArticlesView_thenReturnArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        BDDMockito.given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

        // When
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("articleComments"));

        // Then
        BDDMockito.then(articleService).should().getArticle(articleId);
    }


    @Disabled("구현 중")
    @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothiing_whenRequestingArticleSearchView_thenReturnArticleSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search"));
    }

    @Disabled("구현 중")
    @DisplayName("[view] [GET] 게시글 해시태그 전용 페이지 - 정상 호출")
    @Test
    public void givenNothiing_whenRequestingArticleHashtagSearchView_thenReturnArticleHashtagSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "ansj",
                LocalDateTime.now(),
                "ansj"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "ansj",
                "pw",
                "ansj@mail.com",
                "Ansj",
                "memo",
                LocalDateTime.now(),
                "ansj",
                LocalDateTime.now(),
                "ansj"
        );
    }
}
