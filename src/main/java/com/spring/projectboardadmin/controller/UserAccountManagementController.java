package com.spring.projectboardadmin.controller;

import com.spring.projectboardadmin.dto.response.UserAccountResponse;
import com.spring.projectboardadmin.service.UserAccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/management/user-accounts")
@Controller
public class UserAccountManagementController {
    private final UserAccountManagementService userAccountManagementService;
    @GetMapping
    public String userAccounts(
            @PageableDefault(size=10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        model.addAttribute(
          "userAccounts",
          userAccountManagementService.getUserAccounts().stream().map(UserAccountResponse::of).toList()
        );
        return "management/user-accounts";
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public UserAccountResponse userAccount(@PathVariable String userId) {
        return UserAccountResponse.of(
            userAccountManagementService.getUserAccount(userId)
        );
    }

    @PostMapping("/{userId}/delete")
    public String deleteUserAccount(@PathVariable String userId) {
        userAccountManagementService.deleteUserAccount(userId);

        return "redirect:/management/user-accounts";
    }

}
