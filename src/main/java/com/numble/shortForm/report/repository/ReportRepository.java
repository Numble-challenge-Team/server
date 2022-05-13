package com.numble.shortForm.report.repository;

import com.numble.shortForm.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
}
