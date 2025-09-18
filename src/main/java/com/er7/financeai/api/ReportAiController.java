package com.er7.financeai.api;

import com.er7.financeai.domain.model.ReportAI;
import com.er7.financeai.domain.repository.projection.ReportAiResume;
import com.er7.financeai.domain.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/report-ai")
public class ReportAiController {

    private final OpenAiService openAiService;

    public ReportAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping
    public ReportAI reportAi(Authentication authentication) {
        String userId = authentication.getName();
        return openAiService.buildReportAi(userId);
    }

    @GetMapping("/resume")
    public ResponseEntity<List<ReportAiResume>> getReportsResume(Authentication authentication) {
        var reportsResume = openAiService.getReportsResume(authentication.getName());
        return ResponseEntity.ok(reportsResume);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportAI> getReportsResume(@PathVariable Long reportId, Authentication authentication) {
        var report = openAiService.getReportAiById(reportId, authentication.getName());
        return ResponseEntity.ok(report);
    }
}
