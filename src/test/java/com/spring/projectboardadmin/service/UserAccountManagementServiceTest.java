package com.spring.projectboardadmin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.projectboardadmin.dto.ArticleCommentDto;
import com.spring.projectboardadmin.dto.UserAccountDto;
import com.spring.projectboardadmin.dto.properties.ProjectProperties;
import com.spring.projectboardadmin.dto.response.ArticleCommentClientResponse;
import com.spring.projectboardadmin.dto.response.UserAccountClientResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 회원 관리")
class UserAccountManagementServiceTest {

    @Disabled("실제 API 호출 결과 확인용")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealApiTest {
        private final UserAccountManagementService sut;

        @Autowired
        public RealApiTest(UserAccountManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("댓글 목록 호출 실제 API")
        @Test
        void callingUserAccountsRealApi() {
            // Given

            // When
            List<UserAccountDto> result = sut.getUserAccounts();
            // Then
            System.out.println(result.stream().findFirst());
            assertThat(result).isNotNull();
        }
    }

    @DisplayName("API mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(UserAccountManagementService.class)
    @Nested
    class restTemplateTest {
        private final UserAccountManagementService sut;
        private final ProjectProperties projectProperties;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public restTemplateTest(
                UserAccountManagementService sut,
                ProjectProperties projectProperties,
                MockRestServiceServer server,
                ObjectMapper mapper
        ) {
            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("회원 목록 호출 API")
        @Test
        void callingUserAccountsApi() throws JsonProcessingException {
            // Given
            UserAccountDto expectedUserAccount = createUserAccountDto("joo", "Joo");
            UserAccountClientResponse expectedResponse = UserAccountClientResponse.of(List.of(expectedUserAccount));
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/userAccounts?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));
            // When
            List<UserAccountDto> result = sut.getUserAccounts();
            // Then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("userId", expectedUserAccount.userId())
                    .hasFieldOrPropertyWithValue("nickname", expectedUserAccount.nickname());
            server.verify();
        }

        @DisplayName("회원 호출 API")
        @Test
        void callingUserAccountApi() throws JsonProcessingException {
            // Given
            String userId = "joo";
            UserAccountDto expectedUserAccount = createUserAccountDto("joo", "Joo");
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/userAccounts/" + userId))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedUserAccount),
                            MediaType.APPLICATION_JSON
                    ));
            // When
            UserAccountDto result = sut.getUserAccount(userId);
            // Then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("userId", expectedUserAccount.userId())
                    .hasFieldOrPropertyWithValue("nickname", expectedUserAccount.nickname());
            server.verify();
        }

        @DisplayName("회원 삭제 API")
        @Test
        void deleteUserAccountApi() {
            // Given
            String userId = "joo";
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/userAccounts/" + userId + "/delete"))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());
            // When
            sut.deleteUserAccount(userId);
            // Then
            server.verify();
        }

        private UserAccountDto createUserAccountDto(String userId, String nickname) {
            return UserAccountDto.of(
                    userId,
                    "joo-test@email.com",
                    nickname,
                    "test memo"
            );
        }
    }
}