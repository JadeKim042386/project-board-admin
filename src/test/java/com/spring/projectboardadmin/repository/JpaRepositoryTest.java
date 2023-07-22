package com.spring.projectboardadmin.repository;

import com.spring.projectboardadmin.domain.AdminAccount;
import com.spring.projectboardadmin.domain.constant.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final AdminAccountRepository adminAccountRepository;

    public JpaRepositoryTest(@Autowired AdminAccountRepository adminAccountRepository) {
        this.adminAccountRepository = adminAccountRepository;
    }

    @DisplayName("회원 정보 조회")
    @Test
    void getAdminAccount() {
        // Given

        // When
        List<AdminAccount> adminAccounts = adminAccountRepository.findAll();
        // Then
        assertThat(adminAccounts).isNotNull().hasSize(4);
    }

    @DisplayName("회원 정보 추가")
    @Test
    void addAdminAccount() {
        // Given
        long previousCount = adminAccountRepository.count();
        AdminAccount adminAccount = AdminAccount.of("test", "pw", Set.of(RoleType.DEVELOPER), null, null, null);
        // When
        adminAccountRepository.save(adminAccount);
        // Then
        assertThat(adminAccountRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("회원 정보 수정")
    @Test
    void updateAdminAccount() {
        // Given
        AdminAccount adminAccount = adminAccountRepository.getReferenceById("joo");
        adminAccount.addRoleType(RoleType.DEVELOPER);
        adminAccount.addRoleTypes(List.of(RoleType.USER, RoleType.USER));
        adminAccount.removeRoleType(RoleType.ADMIN);
        // When
        AdminAccount updatedAccount = adminAccountRepository.saveAndFlush(adminAccount);
        // Then
        assertThat(updatedAccount)
                .hasFieldOrPropertyWithValue("userId", "joo")
                .hasFieldOrPropertyWithValue("roleTypes", Set.of(RoleType.DEVELOPER, RoleType.USER));
    }

    @DisplayName("회원 정보 삭제")
    @Test
    void deleteAdminAccount() {
        // Given
        long previousCount = adminAccountRepository.count();
        AdminAccount adminAccount = adminAccountRepository.getReferenceById("joo");
        // When
        adminAccountRepository.delete(adminAccount);
        // Then
        assertThat(adminAccountRepository.count()).isEqualTo(previousCount - 1);
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
