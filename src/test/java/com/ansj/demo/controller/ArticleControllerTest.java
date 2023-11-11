package com.ansj.demo.controller;

import com.ansj.demo.config.SecurityConfig;
import com.ansj.demo.config.TestSecurityConfig;
import com.ansj.demo.domain.constant.FormStatus;
import com.ansj.demo.domain.constant.SearchType;
import com.ansj.demo.dto.ArticleDto;
import com.ansj.demo.dto.ArticleWithCommentsDto;
import com.ansj.demo.dto.UserAccountDto;
import com.ansj.demo.dto.request.ArticleRequest;
import com.ansj.demo.dto.response.ArticleResponse;
import com.ansj.demo.dto.security.BoardPrincipal;
import com.ansj.demo.service.ArticleService;
import com.ansj.demo.service.PaginationService;
import com.ansj.demo.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("view 컨트롤러 - 게시글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private ArticleService articleService;
    @MockBean
    private PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
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
        BDDMockito.given(paginationService.getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt()))
                .willReturn(List.of(0, 1, 2, 3, 4));

        // When
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        // Then
        BDDMockito.then(articleService).should().searchArticles(BDDMockito.eq(null),
                BDDMockito.eq(null),
                BDDMockito.any(Pageable.class)
        );
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt());
    }

    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
    @Test
    public void givenSearchKeyword_whenSearchingArticlesView_thenReturnArticlesView() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        BDDMockito.given(articleService.searchArticles(
                BDDMockito.eq(searchType),
                BDDMockito.eq(searchValue),
                BDDMockito.any(Pageable.class)
        )).willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt()))
                .willReturn(List.of(0, 1, 2, 3, 4));

        // When
        mvc.perform(get("/articles").queryParam("searchType", searchType.name()).queryParam("searchValue", searchValue))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));
//                .andExpect(model().attributeExists("paginationBarNumbers"));

        // Then
        BDDMockito.then(articleService).should().searchArticles(
                BDDMockito.eq(searchType),
                BDDMockito.eq(searchValue),
                BDDMockito.any(Pageable.class)
        );
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판)페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesView() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        BDDMockito.given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages()))
                .willReturn(barNumbers);

        // When
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));

        // Then
        BDDMockito.then(articleService).should().searchArticles(null, null, pageable);
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시글 페이지 - 인증 없을 땐로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequestingArticlePage_thenRedirectsToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Then
        BDDMockito.then(articleService).shouldHaveNoInteractions();
        BDDMockito.then(articleService).shouldHaveNoInteractions();

    }

    /**@WithMockUser
     * 모킹한 유저 정보가, 내가 실제로 구현한 인증 정보(BoardPrincipal)와 상관이 없을 때,
     * 즉 컨트롤러 레이어에서 인증 정보를 받아서 뭔가 추가로 수행하는 일이 없을 때에는 이 방식이 좋음.
     * 하지만, 실제 SecurityConfig 를 사용하지 않으니 실제 사용자 정보를 이용할 수 없다는 단점이 존재한다.
     */
    @WithMockUser
    @DisplayName("[view] [GET] 게시글 페이지 - 정상 호출, 인증된 사용자.")
    @Test
    public void givenNothiing_whenRequestingArticlesView_thenReturnArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        Long totalCount = 1L;
        BDDMockito.given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());

        // When는
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("articleComments"));

        // Then
        BDDMockito.then(articleService).should().getArticleWithComments(articleId);
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

    @DisplayName("[view] [GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void givenNothiing_whenRequestingArticleSearchHashtagView_thenReturnArticleSearchHashtagView() throws Exception {
        // Given
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        BDDMockito.given(articleService.searchArticlesViaHashtag(
                BDDMockito.eq(null), BDDMockito.any(Pageable.class)
        )).willReturn(Page.empty());
        BDDMockito.given(articleService.getHashtags()).willReturn(hashtags);
        BDDMockito.given(paginationService.getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt()))
                .willReturn(List.of(1,2,3,4,5));

        // When
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        // Then
        BDDMockito.then(articleService).should().searchArticlesViaHashtag(BDDMockito.eq(null), BDDMockito.any(Pageable.class));
        BDDMockito.then(articleService).should().getHashtags();
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt());
    }

    @DisplayName("[view] [GET] 게시글 해시태그 검색 페이지 - 정상 호출, 해시태그 입력")
    @Test
    public void givenHashtag_whenRequestingArticleSearchHashtagView_thenReturnArticleSearchHashtagView() throws Exception {
        // Given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        BDDMockito.given(articleService.searchArticlesViaHashtag(
                BDDMockito.eq(hashtag), BDDMockito.any(Pageable.class)
        )).willReturn(Page.empty());
        BDDMockito.given(articleService.getHashtags()).willReturn(hashtags);
        BDDMockito.given(paginationService.getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt()))
                .willReturn(List.of(1,2,3,4,5));

        // When
        mvc.perform(get("/articles/search-hashtag").queryParam("searchValue", hashtag))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));

        // Then
        BDDMockito.then(articleService).should().searchArticlesViaHashtag(BDDMockito.eq(hashtag), BDDMockito.any(Pageable.class));
        BDDMockito.then(articleService).should().getHashtags();
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(BDDMockito.anyInt(), BDDMockito.anyInt());
    }

    @WithMockUser
    @DisplayName("[view][GET] 새 게시글 작성 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value = "ansjtest", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        // Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        BDDMockito.willDoNothing().given(articleService).saveArticle(BDDMockito.any(ArticleDto.class));

        // When & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        BDDMockito.then(articleService).should().saveArticle(BDDMockito.any(ArticleDto.class));
    }

    @DisplayName("[view][GET] 게시글 수정 페이지 - 인증 없을 땐로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        BDDMockito.then(articleService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 수정 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        BDDMockito.given(articleService.getArticle(articleId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        BDDMockito.then(articleService).should().getArticle(articleId);
    }

    @WithUserDetails(value = "ansjtest", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesNewArticle() throws Exception {
        // Given
        long articleId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        BDDMockito.willDoNothing().given(articleService).updateArticle(BDDMockito.eq(articleId), BDDMockito.any(ArticleDto.class));

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        BDDMockito.then(articleService).should().updateArticle(BDDMockito.eq(articleId), BDDMockito.any(ArticleDto.class));
    }

    @WithUserDetails(value = "ansjtest", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "ansjtest";
        BDDMockito.willDoNothing().given(articleService).deleteArticle(articleId, userId);

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        BDDMockito.then(articleService).should().deleteArticle(articleId, userId);
    }


    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                createUserAccountDto(),
                "title",
                "content",
                "#java"
        );
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
