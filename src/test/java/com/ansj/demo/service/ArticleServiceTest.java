package com.ansj.demo.service;

import com.ansj.demo.controller.ArticleController;
import com.ansj.demo.domain.Article;
import com.ansj.demo.domain.type.SearchType;
import com.ansj.demo.dto.ArticleDto;
import com.ansj.demo.dto.ArticleUpdateDto;
import com.ansj.demo.repository.ArticleRepository;
import org.assertj.core.internal.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    // mock 을 주입하는 대상을 InjectMock 이라는 애노테이션으로 붙여준다. 그외 나머지 mock 은 Mock 이라는 애노테이션으로 표현한다.
    @InjectMocks private ArticleService sut;// System Under Test, 테스트 대상을 의미함. 테스트 짤때많이 사용하는 네이밍 중에 하나이다.
    @Mock private ArticleRepository articleRepository; // 테스트 대상이 의존하는 객체를 또 하나 가져온다(모킹할 때 필요함)

    /*
        검색
        각 게시글 페이지로 이동
        페이지네이션
        홈 버튼 -> 게시판 페이지로 리다이렉션
        정렬 기능
     */

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() {
        // Given

        // When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword"); // 제목, 본문, ID, 닉네임, 해시태그

        // Then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // Given

        // When
        ArticleDto article = sut.searchArticle(1L); // 제목, 본문, ID, 닉네임, 해시태그

        // Then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSaveArticle() {
        // Given
        BDDMockito.given(articleRepository.save(ArgumentMatchers.any(Article.class))).willReturn(null);

        // When
        sut.saveArticle(ArticleDto.of(LocalDateTime.now(), "Ansj", "title", "content", "#java"));

        // Then
        BDDMockito.then(articleRepository).should().save(ArgumentMatchers.any(Article.class));
        // 테스트가 데이터베이스 레이어까지 내려가게 되면, 그것은 더 이상 유닛 테스트가 아닌 sociable 테스트라고 한다.
        // 유닛 테스트는 solitary 테스트라고 함.
    }

    @DisplayName("게시글의 ID 와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        BDDMockito.given(articleRepository.save(ArgumentMatchers.any(Article.class))).willReturn(null);

        // When
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content", "#java"));

        // Then
        BDDMockito.then(articleRepository).should().save(ArgumentMatchers.any(Article.class));
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeleteArticle() {
        // Given
        BDDMockito.willDoNothing().given(articleRepository).delete(ArgumentMatchers.any(Article.class));

        // When
        sut.deleteArticle(1L);

        // Then
        BDDMockito.then(articleRepository).should().delete(ArgumentMatchers.any(Article.class));
    }

}