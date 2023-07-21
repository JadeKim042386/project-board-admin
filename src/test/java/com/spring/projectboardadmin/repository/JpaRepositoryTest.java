package com.spring.projectboardadmin.repository;

import com.spring.projectboardadmin.domain.UserAccount;
import com.spring.projectboardadmin.domain.constant.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(@Autowired UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("회원 정보 조회")
    @Test
    void getUserAccount() {
        // Given

        // When
        List<UserAccount> userAccounts = userAccountRepository.findAll();
        // Then
        assertThat(userAccounts).isNotNull().hasSize(4);
    }

    @DisplayName("회원 정보 추가")
    @Test
    void addUserAccount() {
        // Given
        long previousCount = userAccountRepository.count();
        UserAccount userAccount = UserAccount.of("test", "pw", Set.of(RoleType.DEVELOPER), null, null, null);
        // When
        userAccountRepository.save(userAccount);
        // Then
        assertThat(userAccountRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("회원 정보 수정")
    @Test
    void updateUserAccount() {
        // Given
        UserAccount userAccount = userAccountRepository.getReferenceById("joo");
        userAccount.addRoleType(RoleType.DEVELOPER);
        userAccount.addRoleTypes(List.of(RoleType.USER, RoleType.USER));
        userAccount.removeRoleType(RoleType.ADMIN);
        // When
        UserAccount updatedAccount = userAccountRepository.saveAndFlush(userAccount);
        // Then
        assertThat(updatedAccount)
                .hasFieldOrPropertyWithValue("userId", "joo")
                .hasFieldOrPropertyWithValue("roleTypes", Set.of(RoleType.DEVELOPER, RoleType.USER));
    }

    @DisplayName("회원 정보 삭제")
    @Test
    void deleteUserAccount() {
        // Given
        long previousCount = userAccountRepository.count();
        UserAccount userAccount = userAccountRepository.getReferenceById("joo");
        // When
        userAccountRepository.delete(userAccount);
        // Then
        assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
    }

    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("joo");
        }
    }
}
