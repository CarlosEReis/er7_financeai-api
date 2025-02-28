package com.er7.financeai.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class ReportAI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy-MM")
    private OffsetDateTime referentMonth;

    @Lob
    private String report;

    @PrePersist
    public void referentMonth() {
        this.referentMonth = OffsetDateTime.now().minusMonths(1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getReferentMonth() {
        return referentMonth;
    }

    public void setReferentMonth(OffsetDateTime referentMonth) {
        this.referentMonth = referentMonth;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReportAI reportAI = (ReportAI) o;
        return Objects.equals(id, reportAI.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
