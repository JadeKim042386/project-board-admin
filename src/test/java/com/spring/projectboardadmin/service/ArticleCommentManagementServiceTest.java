package com.spring.projectboardadmin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.projectboardadmin.domain.constant.RoleType;
import com.spring.projectboardadmin.dto.ArticleCommentDto;
import com.spring.projectboardadmin.dto.UserAccountDto;
import com.spring.projectboardadmin.dto.properties.ProjectProperties;
import com.spring.projectboardadmin.dto.response.ArticleCommentClientResponse;
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
@DisplayName("비지니스 로직 - 댓글 관리")
class ArticleCommentManagementServiceTest {

    @Disabled("실제 API 호출 결과 확인용")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealApiTest {
        private final ArticleCommentManagementService sut;

        @Autowired
        public RealApiTest(ArticleCommentManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("댓글 목록 호출 실제 API")
        @Test
        void callingArticleCommentsRealApi() {
            // Given

            // When
            List<ArticleCommentDto> result = sut.getArticleComments();
            // Then
            System.out.println(result.stream().findFirst());
            assertThat(result).isNotNull();
        }

        @DisplayName("댓글 1개 호출 실제 API")
        @Test
        void callingArticleCommentRealApi() {
            // Given
            Long articleCommentId = 1L;
            // When
            ArticleCommentDto result = sut.getArticleComment(articleCommentId);
            // Then
            System.out.println(result);
            assertThat(result).isNotNull();
        }
    }

    @DisplayName("API mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(ArticleCommentManagementService.class)
    @Nested
    class restTemplateTest {
        private final ArticleCommentManagementService sut;
        private final ProjectProperties projectProperties;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public restTemplateTest(
                ArticleCommentManagementService sut,
                ProjectProperties projectProperties,
                MockRestServiceServer server,
                ObjectMapper mapper
        ) {
            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("댓글 목록 호출 API")
        @Test
        void callingArticleCommentsApi() throws JsonProcessingException {
            // Given
            ArticleCommentDto expectedArticleComment = createArticleCommentDto("content");
            ArticleCommentClientResponse expectedResponse = ArticleCommentClientResponse.of(List.of(expectedArticleComment));
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articleComments?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));
            // When
            List<ArticleCommentDto> result = sut.getArticleComments();
            // Then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("id", expectedArticleComment.id())
                    .hasFieldOrPropertyWithValue("content", expectedArticleComment.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticleComment.userAccount().nickname());
            server.verify();
        }

        @DisplayName("댓글 호출 API")
        @Test
        void callingArticleApi() throws JsonProcessingException {
            // Given
            Long articleCommentId = 1L;
            ArticleCommentDto expectedArticleComment = createArticleCommentDto("content");
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articleComments/" + articleCommentId + "?projection=withUserAccount"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedArticleComment),
                            MediaType.APPLICATION_JSON
                    ));
            // When
            ArticleCommentDto result = sut.getArticleComment(articleCommentId);
            // Then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("id", expectedArticleComment.id())
                    .hasFieldOrPropertyWithValue("content", expectedArticleComment.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticleComment.userAccount().nickname());
            server.verify();
        }

        @DisplayName("댓글 삭제 API")
        @Test
        void deleteArticleApi() {
            // Given
            long articleCommentId = 1L;
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articleComments/" + articleCommentId + "/delete"))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());
            // When
            sut.deleteArticleComment(articleCommentId);
            // Then
            server.verify();
        }

        private ArticleCommentDto createArticleCommentDto(String content) {
            return ArticleCommentDto.of(
                    1L,
                    1L,
                    createUserAccountDto(),
                    null,
                    content,
                    LocalDateTime.now(),
                    "Uno",
                    LocalDateTime.now(),
                    "Uno"
            );
        }

        private UserAccountDto createUserAccountDto() {
            return UserAccountDto.of(
                    "unoTest",
                    "uno-test@email.com",
                    "uno-test",
                    "test memo"
            );
        }
    }
}
