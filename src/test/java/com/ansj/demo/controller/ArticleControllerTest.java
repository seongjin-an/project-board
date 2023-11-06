package com.ansj.demo.controller;

import com.ansj.demo.config.SecurityConfig;
import com.ansj.demo.domain.type.SearchType;
import com.ansj.demo.dto.ArticleWithCommentsDto;
import com.ansj.demo.dto.UserAccountDto;
import com.ansj.demo.service.ArticleService;
import com.ansj.demo.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("view 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private final MockMvc mvc;

    @MockBean
    private ArticleService articleService;
    @MockBean
    private PaginationService paginationService;

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
