package com.spring.projectboardadmin.config;

import com.spring.projectboardadmin.service.VisitCounterService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

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
