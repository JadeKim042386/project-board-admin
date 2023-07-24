package com.spring.projectboardadmin.config;

import com.spring.projectboardadmin.domain.constant.RoleType;
import com.spring.projectboardadmin.dto.AdminAccountDto;
import com.spring.projectboardadmin.service.AdminAccountService;
import com.spring.projectboardadmin.service.VisitCounterService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class GlobalControllerConfig {
    @MockBean
    private VisitCounterService visitCounterService;

    @BeforeTestMethod
    public void securitySetUp() {
        given(visitCounterService.visitCount())
                .willReturn(0L);
    }
}
