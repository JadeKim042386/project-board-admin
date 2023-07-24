package com.spring.projectboardadmin.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("project")
public record ProjectProperties(Board board) {
    /**
     * 게시판 속성
     *
     * @param url 게시판 서비스 호스트
     */
    public record Board(String url) {}
}
