package com.spring.projectboardadmin.service;

import com.spring.projectboardadmin.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArticleManagementService {
    public List<ArticleDto> getArticles() {
        return List.of();
    }
    public ArticleDto getArticle(int articleIndex, int pageNumber) {
        return null;
    }
    public void deleteArticle(Long articleId) {

    }
}
