package com.spring.projectboardadmin.controller;

import com.spring.projectboardadmin.dto.response.AdminAccountResponse;
import com.spring.projectboardadmin.service.AdminAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AdminAccountController {
    private final AdminAccountService adminAccountService;
    @GetMapping("/admin/members")
    public String adminUserAccounts() {
        return "admin/members";
    }

    @ResponseBody
    @GetMapping("/api/admin/members")
    public List<AdminAccountResponse> getMembers() {
        return adminAccountService.users().stream()
                .map(AdminAccountResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    @DeleteMapping("/api/admin/members/{userId}/delete")
    public void deleteMember(@PathVariable String userId) {
        adminAccountService.deleteUser(userId);
    }

}
