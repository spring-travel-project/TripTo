package com.tripto.dao;

import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDAO {
    @Autowired
    private SqlSessionTemplate sql;

    public Map<String, Object> getStats() {
        return sql.selectOne("admin.stats");
    }
}