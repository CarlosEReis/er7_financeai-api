package com.er7.financeai.api;

import com.er7.financeai.service.OpenAiService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/report-ai")
public class ReportAiController {

    private final OpenAiService openAiService;

    public ReportAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping
    public String reportAi(Authentication authentication) {
        String userId = authentication.getName();
        return openAiService.generateAiReport(userId);
    }
}
