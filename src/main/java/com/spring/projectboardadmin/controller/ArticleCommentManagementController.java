package com.spring.projectboardadmin.controller;

import com.spring.projectboardadmin.dto.ArticleCommentDto;
import com.spring.projectboardadmin.dto.response.ArticleCommentResponse;
import com.spring.projectboardadmin.dto.response.ArticleResponse;
import com.spring.projectboardadmin.service.ArticleCommentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/management/article-comments")
@Controller
public class ArticleCommentManagementController {
    private final ArticleCommentManagementService articleCommentManagementService;
    @GetMapping
    public String articleComments(Model model) {
        model.addAttribute(
                "comments",
                articleCommentManagementService.getArticleComments().stream().map(ArticleCommentResponse::of).toList()
        );
        return "management/article-comments";
    }

    @ResponseBody
    @GetMapping("/{id}")
    public ArticleCommentResponse articleComment(@PathVariable Long id) {
        return ArticleCommentResponse.of(
            articleCommentManagementService.getArticleComment(id)
        );
    }

    @PostMapping("/{id}/delete")
    public String deleteArticleComment(@PathVariable Long id) {
        articleCommentManagementService.deleteArticleComment(id);

        return "redirect:/management/article-comments";
    }
}
