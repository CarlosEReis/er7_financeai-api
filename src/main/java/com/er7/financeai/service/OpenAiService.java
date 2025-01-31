package com.er7.financeai.service;

import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.projection.TransactionsReportAi;
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
    private final TransactionRepository transactionRepository;

    private MessageSource messageSource;

    @Value("${financeai.openai.chat.system.msg}")
    private String messageSystem;

    @Value("${financeai.openai.chat.user.msg.prefix}")
    private String messageUserPrefix;

    OpenAiService(ChatClient.Builder chartClient, TransactionRepository transactionRepository1, MessageSource messageSource) {
        this.chatClient = chartClient.build();
        this.transactionRepository = transactionRepository1;
        this.messageSource = messageSource;
    }

    public String generateAiReport(String userId) {
        String transactionsForAi = transactionsDataLoadForReport(userId);
        String messageUserFinal = messageUserPrefix.concat(transactionsForAi);
        return chatClient
            .prompt()
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
        OffsetDateTime now = OffsetDateTime.now();
        var startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();
        var endDate = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
}
