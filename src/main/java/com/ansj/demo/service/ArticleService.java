package com.ansj.demo.service;

import com.ansj.demo.domain.type.SearchType;
import com.ansj.demo.dto.ArticleDto;
import com.ansj.demo.dto.ArticleUpdateDto;
import com.ansj.demo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

//    @Transactional(readOnly = true)
//    public List<ArticleDto> searchArticles(SearchType searchType, String searchKeyword) {
//        return List.of();
//    }
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(long l) {
        return null;
    }

    public void saveArticle(ArticleDto dto) {

    }

    public void updateArticle(long articleId, ArticleUpdateDto dto) {

    }

    public void deleteArticle(long articleId) {

    }
}
