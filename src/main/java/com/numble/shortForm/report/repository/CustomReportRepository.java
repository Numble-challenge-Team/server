package com.numble.shortForm.report.repository;

import com.numble.shortForm.report.dto.response.ReportResponseByVideo;
import com.numble.shortForm.report.dto.response.reportResponseByUser;

import java.util.List;

public interface CustomReportRepository {

    public List<reportResponseByUser> reportListUserBy(int userId);

    public List<ReportResponseByVideo> reportListByVideo();

}
