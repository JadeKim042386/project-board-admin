package com.spring.projectboardadmin.controller;

import com.spring.projectboardadmin.config.GlobalControllerConfig;
import com.spring.projectboardadmin.config.SecurityConfig;
import com.spring.projectboardadmin.domain.constant.RoleType;
import com.spring.projectboardadmin.dto.AdminAccountDto;
import com.spring.projectboardadmin.service.AdminAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 - 게시글 관리")
@Import({SecurityConfig.class, GlobalControllerConfig.class})
@WebMvcTest(AdminAccountController.class)
class AdminAccountControllerTest {
    private final MockMvc mvc;
    @MockBean private AdminAccountService adminAccountService;

    @Autowired
    public AdminAccountControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @BeforeTestMethod
    public void securitySetUp() {
        given(adminAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createAdminAccountDto()));
        given(adminAccountService.saveUser(anyString(), anyString(), anySet(), anyString(), anyString(), anyString()))
                .willReturn(createAdminAccountDto());
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[view][GET] Admin 회원 페이지 - 정상 호출")
    @Test
    void requestAdminAccountView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("admin/members"));
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[data][GET] Admin 회원 목록 - 정상 호출")
    @Test
    void requestAdminAccounts() throws Exception {
        // Given
        given(adminAccountService.users()).willReturn(List.of());
        // When
        mvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        // Then
        then(adminAccountService).should().users();
    }

    @WithMockUser(username = "tester", roles = "MANAGER")
    @DisplayName("[data][DELETE] Admin 회원 삭제 - 정상 호출")
    @Test
    void deleteAdminAccounts() throws Exception {
        // Given
        String username = "joo";
        willDoNothing().given(adminAccountService).deleteUser(username);
        // When
        mvc.perform(
                delete("/api/admin/members/" + username + "/delete")
                        .with(csrf())
        )
                .andExpect(status().isNoContent());

        // Then
        then(adminAccountService).should().deleteUser(username);
    }

    private AdminAccountDto createAdminAccountDto() {
        return AdminAccountDto.of(
                "unoTest",
                "pw",
                Set.of(RoleType.USER),
                "uno-test@email.com",
                "uno-test",
                "test memo"
        );
    }
}
