package com.ansj.demo.repository.querydsl;


import java.util.List;

public interface ArticleRepositoryCustom {
    List<String> findAllDistinctHashtags();
}
