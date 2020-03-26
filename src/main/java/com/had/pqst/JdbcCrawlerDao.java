package com.had.pqst;

import java.sql.SQLException;

public interface JdbcCrawlerDao {
    String getNextLink(String sql) throws SQLException;

    void updateLinkToDataBase(String link, String sql) throws SQLException;

    void insertNewsIntoDataBase(String title, String content, String url);

    boolean isProcessed(String link) throws SQLException;

    String getLinkThenDelete() throws SQLException;
}
