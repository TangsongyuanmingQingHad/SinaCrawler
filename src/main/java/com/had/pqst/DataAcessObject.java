package com.had.pqst;

import java.sql.*;

public class DataAcessObject implements JdbcCrawlerDao{
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    private final Connection connection;

    public DataAcessObject() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:h2:file:/Users/had/IdeaProjects/sinaCrawler/newss",
                    USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNextLink(String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    //将分析完的新闻页面放入已处理列表
    public void updateLinkToDataBase(String link, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

    public void insertNewsIntoDataBase(String title, String content, String url) {
        try (PreparedStatement statement = connection.prepareStatement("insert into SINANEWS (title, content, url, create_at, modify_at) values ( ?,?,?,now(),now() ) ")) {
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setString(3, url);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLinkThenDelete() throws SQLException {
        //从数据库中读取带爬取的路径
        String link = getNextLink("select * from LINK_TO_BE_PROCESSED LIMIT 1");
        if (link != null) {
            //从数据库中删除删除这次处理过的url
            updateLinkToDataBase(link, "delete from LINK_TO_BE_PROCESSED where link = ? ");
            return link;
        }
        return null;
    }

    //判断是否是已经处理过的页面
    public boolean isProcessed(String link) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select LINK from LINK_ALREADY_PROCESSED where LINK = ? ")) {
            statement.setString(1, link);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }
}
