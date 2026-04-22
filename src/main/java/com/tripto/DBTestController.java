package com.tripto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DBTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping(value = "/dbtest2", produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public String dbTest2() {

        String sql = "select sysdate from dual";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement pstat = conn.prepareStatement(sql);
            ResultSet rs = pstat.executeQuery()
        ) {
            if (rs.next()) {
                return "DB 연결 및 쿼리 성공: " + rs.getString(1);
            }
            return "쿼리 실행됨, 결과 없음";
        } catch (Exception e) {
            e.printStackTrace();
            return "DB 연결/쿼리 실패: " + e.getMessage();
        }
    }
}