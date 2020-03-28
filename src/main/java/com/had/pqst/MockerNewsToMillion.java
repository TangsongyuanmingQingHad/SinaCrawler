package com.had.pqst;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.h2.engine.Session;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * 复制数据
 */
public class MockerNewsToMillion {
    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory;
        try {
            String resource = "db/mybatis/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MockData(sqlSessionFactory,50000);
        System.out.println("完成");
    }

    public static void MockData(SqlSessionFactory sqlSessionFactory,int number) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            List<SINANEWS> list = session.selectList("com.had.pst.Mock.selectMockNews");
            System.out.println("");
            Random random = new Random();
            int rollBackNumber = 0;
            int count = number - list.size();
            try {
                while (count-- > 0) {
                    int index = random.nextInt(list.size());
                    SINANEWS newsToBeInsert = list.get(index);
                    Instant currentTime = newsToBeInsert.getCreateAt();
                    currentTime = currentTime.minusSeconds(3600 * 24 * 365);
                    newsToBeInsert.setCreateAt(currentTime);
                    newsToBeInsert.setModifyAt(currentTime);
                    session.insert("com.had.pst.Mock.insertMockeNews", newsToBeInsert);
                    System.out.println("SurplusNumber"+count);
//                    if(count%2000==0) {
//                        session.flushStatements();
//                    }
                }
                session.commit();
            } catch (Exception e) {
                session.rollback();
            }
        }
    }
}
