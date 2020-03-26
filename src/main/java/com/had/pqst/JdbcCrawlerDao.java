package com.had.pqst;

import java.sql.SQLException;

public interface JdbcCrawlerDao {
    void updateLinkToDataBase(String link, String sql) throws SQLException;

    void insertNewsIntoDataBase(String title, String content, String url);

    boolean isProcessed(String link) throws SQLException;

    String getLinkThenDelete() throws SQLException;

    void insertLinkIntoDataBase(String href);

    void insertLinkIntoAlreadyDataBase(String link);
}
