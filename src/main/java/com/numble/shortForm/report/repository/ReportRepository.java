package com.numble.shortForm.report.repository;

import com.numble.shortForm.report.entity.Report;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>,CustomReportRepository {
}
