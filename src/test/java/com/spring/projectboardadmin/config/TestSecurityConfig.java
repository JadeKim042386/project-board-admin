package com.spring.projectboardadmin.config;

import com.spring.projectboardadmin.domain.constant.RoleType;
import com.spring.projectboardadmin.dto.AdminAccountDto;
import com.spring.projectboardadmin.service.AdminAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean
    private AdminAccountService adminAccountService;

    @BeforeTestMethod
    public void securitySetUp() {
        given(adminAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createUserAccountDto()));
        given(adminAccountService.saveUser(anyString(), anyString(), anySet(), anyString(), anyString(), anyString()))
                .willReturn(createUserAccountDto());
    }

    private AdminAccountDto createUserAccountDto() {
        return AdminAccountDto.of(
                "jooTest",
                "pw",
                Set.of(RoleType.USER),
                "joo-test@gmail.com",
                "joo-test",
                "test memo"
        );
    }
}
