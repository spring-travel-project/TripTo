package com.tripto.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.AdminDAO;

// AdminDAO.java (생략 가능 - 인젝션해서 바로 호출)
	// AdminService.java
	@Service
	public class AdminService {
	    @Autowired
	    private AdminDAO dao;
	
	    public Map<String, Object> getDashboardStats() {
	        return dao.getStats();
	    }
	}
	

