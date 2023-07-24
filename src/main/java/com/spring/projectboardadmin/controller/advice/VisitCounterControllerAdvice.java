package com.spring.projectboardadmin.controller.advice;

import com.spring.projectboardadmin.service.VisitCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class VisitCounterControllerAdvice {
    private final VisitCounterService visitCounterService;

    @ModelAttribute("visitCount")
    public Long visitCount() {
        return visitCounterService.visitCount();
    }
}
