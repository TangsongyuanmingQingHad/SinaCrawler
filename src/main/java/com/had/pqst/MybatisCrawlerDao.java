package com.had.pqst;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class MybatisCrawlerDao implements JdbcCrawlerDao {
    private SqlSessionFactory sqlSessionFactory;

    public MybatisCrawlerDao() {
        try {
            String resource = "db/mybatis/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateLinkToDataBase(String link, String sql) throws SQLException {

    }

    @Override
    public void insertNewsIntoDataBase(String title, String content, String url) {

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.had.pst.Mybatis.insertNewsIntoDataBase", new SINANEWS(title, content, url));
        }
    }

    @Override
    public boolean isProcessed(String link) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            int number = session.selectOne("com.had.pst.Mybatis.isProcessed", link);
            return number != 0;
        }
    }

    @Override
    public synchronized String getLinkThenDelete() throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String link = session.selectOne("com.had.pst.Mybatis.getLink");
            if (link != null) {
                session.delete("com.had.pst.Mybatis.deleteLink", link);
            }
            return link;
        }
    }

    @Override
    public void insertLinkIntoDataBase(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.had.pst.Mybatis.insertLinkIntoDataBase", link);
        }
    }

    @Override
    public void insertLinkIntoAlreadyDataBase(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.had.pst.Mybatis.insertLinkIntoAlreadyDataBase", link);
        }
    }
}
