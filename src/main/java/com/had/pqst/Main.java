package com.had.pqst;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {
    public static void main(String[] args) {
        JdbcCrawlerDao dao = new MybatisCrawlerDao();
        Log log = LogFactory.getLog(Main.class);

        for (int i = 0; i < 8; i++) {
            new CrawlerMain(dao, log).start();
        }
    }
}
