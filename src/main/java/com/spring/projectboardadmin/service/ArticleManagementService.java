package com.spring.projectboardadmin.service;

import com.spring.projectboardadmin.dto.ArticleDto;
import com.spring.projectboardadmin.dto.properties.ProjectProperties;
import com.spring.projectboardadmin.dto.response.ArticleClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ArticleManagementService {
    private final RestTemplate restTemplate;
    private final ProjectProperties projectProperties;

    public List<ArticleDto> getArticles() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(
                        projectProperties.board().url()
                                + "/api/articles"
                )
                .queryParam("size", 10000)
                .build()
                .toUri();
        ArticleClientResponse response = restTemplate.getForObject(uri, ArticleClientResponse.class);

        return Optional.ofNullable(response)
                .orElseGet(ArticleClientResponse::empty).articles();
    }
    public ArticleDto getArticle(Long articleId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(
                projectProperties.board().url()
                        + "/api/articles/"
                        + articleId
                )
                .queryParam("projection", "withUserAccount")
                .build()
                .toUri();
        ArticleDto response = restTemplate.getForObject(uri, ArticleDto.class);

        return Optional.ofNullable(response)
                .orElseThrow(() -> new NoSuchElementException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void deleteArticle(Long articleId) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(
                        projectProperties.board().url()
                                + "/api/articles/"
                                + articleId
                                + "/delete"
                )
                .build()
                .toUri();
        restTemplate.delete(uri);
    }
}
