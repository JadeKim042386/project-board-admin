package com.spring.projectboardadmin.service;

import com.spring.projectboardadmin.dto.ArticleCommentDto;
import com.spring.projectboardadmin.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArticleCommentManagementService {
    public List<ArticleCommentDto> getArticleComments() {
        return List.of();
    }
    public ArticleCommentDto getArticleComment(Long articleCommentId) {
        return null;
    }
    public void deleteArticleComment(Long articleCommentId) {

    }
}