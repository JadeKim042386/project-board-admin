package com.spring.projectboardadmin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.projectboardadmin.domain.constant.RoleType;
import com.spring.projectboardadmin.dto.ArticleDto;
import com.spring.projectboardadmin.dto.UserAccountDto;
import com.spring.projectboardadmin.dto.properties.ProjectProperties;
import com.spring.projectboardadmin.dto.response.ArticleClientResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 게시글 관리")
class ArticleManagementServiceTest {

    @Disabled("실제 API 호출 결과 확인용")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealApiTest {
    private final ArticleManagementService sut;

    public RealApiTest(@Autowired ArticleManagementService sut) {
        this.sut = sut;
    }

    @DisplayName("게시글 목록 호출 실제 API")
    @Test
    void callingArticlesRealApi() {
        // Given

        // When
        List<ArticleDto> result = sut.getArticles();
        // Then
        System.out.println(result.stream().findFirst());
        assertThat(result).isNotNull();
    }
}

    @DisplayName("API mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(ArticleManagementService.class)
    @Nested
    class restTemplateTest {
        private final ArticleManagementService sut;
        private final ProjectProperties projectProperties;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        public restTemplateTest(
                ArticleManagementService sut,
                ProjectProperties projectProperties,
                MockRestServiceServer server,
                ObjectMapper mapper
        ) {
            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("게시글 목록 호출 API")
        @Test
        void callingArticlesApi() throws JsonProcessingException {
            // Given
            ArticleDto expectedArticle = createArticleDto("제목", "글");
            ArticleClientResponse expectedResponse = ArticleClientResponse.of(List.of(expectedArticle));
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));
            // When
            List<ArticleDto> result = sut.getArticles();
            // Then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("게시글 호출 API")
        @Test
        void callingArticleApi() throws JsonProcessingException {
            // Given
            int articleIndex = 0;
            int pageNumber = 0;
            ArticleDto expectedArticle = createArticleDto("제목", "글");
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles/detail?articleIndex=" + articleIndex + "&page=" + pageNumber))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedArticle),
                            MediaType.APPLICATION_JSON
                    ));
            // When
            ArticleDto result = sut.getArticle(articleIndex, pageNumber);
            // Then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("게시글 삭제 API")
        @Test
        void deleteArticleApi() {
            // Given
            long articleId = 1L;
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles/" + articleId + "/delete"))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());
            // When
            sut.deleteArticle(articleId);
            // Then
            server.verify();
        }

        private ArticleDto createArticleDto(String title, String content) {
            return ArticleDto.of(
                    1L,
                    createUserAccountDto(),
                    title,
                    content,
                    null,
                    LocalDateTime.now(),
                    "Uno",
                    LocalDateTime.now(),
                    "Uno"
            );
        }

        private UserAccountDto createUserAccountDto() {
            return UserAccountDto.of(
                    "unoTest",
                    "pw",
                    Set.of(RoleType.ADMIN),
                    "uno-test@email.com",
                    "uno-test",
                    "test memo"
            );
        }
    }
}