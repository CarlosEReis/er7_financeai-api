package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.ReportAI;
import com.er7.financeai.domain.repository.projection.ReportAiResume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportAiRepository extends JpaRepository<ReportAI, Long> {

    List<ReportAiResume> findAllByUserId(String userId);

    ReportAI findByIdAndUserId(Long id, String userId);
}
