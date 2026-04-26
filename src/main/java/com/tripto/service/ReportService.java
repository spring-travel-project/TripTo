package com.tripto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.ReportDAO;
import com.tripto.dto.ReportDTO;

@Service
public class ReportService {

    @Autowired
    private ReportDAO dao;

    public int add(ReportDTO dto) {

        int count = dao.checkDuplicate(dto);

        if (count > 0) {
            return -1;
        }

        return dao.add(dto);
    }
}