package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.ReportAI;
import com.er7.financeai.domain.repository.ReportAiRepository;
import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.projection.ReportAiResume;
import com.er7.financeai.domain.repository.projection.TransactionsReportAi;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    private final ChatClient chatClient;
    private final ReportAiRepository reportAiRepository;
    private final TransactionRepository transactionRepository;

    private MessageSource messageSource;

    @Value("${financeai.openai.chat.system.msg}")
    private String messageSystem;

    @Value("${financeai.openai.chat.user.msg.prefix}")
    private String messageUserPrefix;

    OpenAiService(ChatClient.Builder chartClient, TransactionRepository transactionRepository1, ReportAiRepository reportAiRepository,MessageSource messageSource) {
        this.chatClient = chartClient.build();
        this.messageSource = messageSource;
        this.reportAiRepository = reportAiRepository;
        this.transactionRepository = transactionRepository1;
    }

    public ReportAI buildReportAi(String userId) {
        String reportAiText = getAiReport(userId);
        ReportAI reportAI = new ReportAI();
        reportAI.setReport(reportAiText);
        reportAI.setUserId(userId);
        return reportAiRepository.save(reportAI);
    }

    public List<ReportAiResume> getReportsResume(String userId) {
        return reportAiRepository.findAllByUserId(userId);
    }

    @Transactional
    public ReportAI getReportAiById(Long reportId, String userId) {
        return reportAiRepository.findByIdAndUserId(reportId, userId);
    }

    public String getAiReport(String userId) {
        String transactionsForAi = transactionsDataLoadForReport(userId);
        String messageUserFinal = messageUserPrefix.concat(transactionsForAi);
        return chatClient
            .prompt() //.options(ChatOptions.builder().maxTokens(100).build())
            .system(messageSystem)
            .user(messageUserFinal)
            .call().content();
    }

    private String transactionsDataLoadForReport(String userId) {
        List<TransactionsReportAi> transactions = getTransactionsForReport(userId);
        var dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return transactions
            .stream()
            .map(t -> String.format(
                "%s-R$%.2f-%s-%s",
                t.getDate().format(dateTimeFormatter),
                t.getAmount(),
                t.getType(),
                t.getCategory().getName()))
            .collect(Collectors.joining(";"));
    }

    private List<TransactionsReportAi> getTransactionsForReport(String userId) {
        OffsetDateTime now = OffsetDateTime.now().minusMonths(1);
        var startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();
        var endDate = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        return transactionRepository.findByUserSubAndDateBetween(userId, startDate, endDate);
    }
}
