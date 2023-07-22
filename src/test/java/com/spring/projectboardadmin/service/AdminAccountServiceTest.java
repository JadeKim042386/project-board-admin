package com.spring.projectboardadmin.service;

import com.spring.projectboardadmin.domain.AdminAccount;
import com.spring.projectboardadmin.domain.constant.RoleType;
import com.spring.projectboardadmin.dto.AdminAccountDto;
import com.spring.projectboardadmin.repository.AdminAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("비지니스 로직 - Admin 회원 관리")
@ExtendWith(MockitoExtension.class)
class AdminAccountServiceTest {
    @InjectMocks private AdminAccountService sut;
    @Mock private AdminAccountRepository adminAccountRepository;

    @DisplayName("어드민 회원 목록 조회")
    @Test
    void getAdminAccounts(){
        // Given
        given(adminAccountRepository.findAll()).willReturn(List.of());
        // When
        List<AdminAccountDto> result = sut.users();
        // Then
        assertThat(result).hasSize(0);
        then(adminAccountRepository).should().findAll();
    }

    @DisplayName("어드민 회원 조회")
    @Test
    void getAdminAccount() {
        // Given
        String username = "joo";
        AdminAccount adminAccount = createAdminAccount(username);
        given(adminAccountRepository.findById(username)).willReturn(Optional.of(adminAccount));
        // When
        Optional<AdminAccountDto> result = sut.searchUser(username);
        // Then
        assertThat(result).isPresent();
        then(adminAccountRepository).should().findById(username);
    }

    @DisplayName("[예외] 존재하지 않는 어드민 회원 조회")
    @Test
    void getNotExistingAdminAccount() {
        // Given
        String username = "wrong-usser";
        given(adminAccountRepository.findById(username)).willReturn(Optional.empty());
        // When
        Optional<AdminAccountDto> result = sut.searchUser(username);
        // Then
        assertThat(result).isEmpty();
        then(adminAccountRepository).should().findById(username);
    }

    @DisplayName("회원 가입")
    @Test
    void joinAdminAccount() {
        // Given
        AdminAccount adminAccount = createSigningUpAdminAccount("joo", Set.of(RoleType.USER));
        given(adminAccountRepository.save(adminAccount)).willReturn(adminAccount);
        // When
        AdminAccountDto result = sut.saveUser(
                adminAccount.getUserId(),
                adminAccount.getUserPassword(),
                adminAccount.getRoleTypes(),
                adminAccount.getEmail(),
                adminAccount.getNickname(),
                adminAccount.getMemo()
        );
        // Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("userId", adminAccount.getUserId())
                .hasFieldOrPropertyWithValue("userPassword", adminAccount.getUserPassword())
                .hasFieldOrPropertyWithValue("roleTypes", adminAccount.getRoleTypes())
                .hasFieldOrPropertyWithValue("email", adminAccount.getEmail())
                .hasFieldOrPropertyWithValue("nickname", adminAccount.getNickname())
                .hasFieldOrPropertyWithValue("memo", adminAccount.getMemo())
                .hasFieldOrPropertyWithValue("createdBy", adminAccount.getUserId())
                .hasFieldOrPropertyWithValue("modifiedBy", adminAccount.getUserId());
        then(adminAccountRepository).should().save(adminAccount);
    }

    @DisplayName("회원 삭제")
    @Test
    void deleteAdminAccount() {
        // Given
        String userId = "joo";
        willDoNothing().given(adminAccountRepository).deleteById(userId);
        // When
        sut.deleteUser(userId);
        // Then
        then(adminAccountRepository).should().deleteById(userId);
    }

    private AdminAccount createAdminAccount(String username) {
        return createAdminAccount(username, Set.of(RoleType.USER), username);
    }

    private AdminAccount createSigningUpAdminAccount(String username, Set<RoleType> roleTypes) {
        return createAdminAccount(username, roleTypes, username);
    }

    private AdminAccount createAdminAccount(String username, Set<RoleType> roleTypes, String createdBy) {
        return AdminAccount.of(
          username,
          "pw",
                roleTypes,
                "joo@gmail.com",
                "Joo",
                "memo",
                createdBy
        );
    }
}
