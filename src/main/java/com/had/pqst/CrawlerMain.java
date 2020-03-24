package com.had.pqst;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class CrawlerMain {
    public static final Log log = LogFactory.getLog(CrawlerMain.class);
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:file:/Users/had/IdeaProjects/sinaCrawler/newss",
                USERNAME, PASSWORD);
        String link;
        //从数据库中加载下一个要爬取的路径
        while ((link = getLinkThenDelete(connection)) != null) {
            if (isProcessed(connection, link)) {
                continue;
            }
            //是否是我们感兴趣的页面
            if (isInteresting(link)) {
                Document doc = getDocumentbyUrl(link);
                parseUrlsFromPagesIntoDataBase(connection, doc);
                storeIntoDataBaseIfItIsNews(doc);
                updateLinkToDataBase(connection, link, "Insert into LINK_ALREADY_PROCESSED (link) VALUES (?)");
            } else {
                //不感兴趣，暂时不处理它
            }
        }
    }

    //将新闻页面的href标签的值存入待处理列表
    private static void parseUrlsFromPagesIntoDataBase(Connection connection, Document doc) throws SQLException {
        for (Element aTarget : doc.select("a")) {
            String href = aTarget.attr("href");
            updateLinkToDataBase(connection, href, "Insert into LINK_TO_BE_PROCESSED (link) VALUES (?)");
        }
    }

    public static String getLinkThenDelete(Connection connection) throws SQLException {
        //从数据库中读取带爬取的路径
        String link = getNextLink(connection, "select * from LINK_TO_BE_PROCESSED LIMIT 1");
        if (link != null) {
            //从数据库中删除删除这次处理过的url
            updateLinkToDataBase(connection, link, "delete from LINK_TO_BE_PROCESSED where link = ? ");
            return link;
        }
        return null;
    }

    //将分析完的新闻页面放入已处理列表
    private static void updateLinkToDataBase(Connection connection, String link, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

    //判断是否是已经处理过的页面
    private static boolean isProcessed(Connection connection, String link) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select LINK from LINK_ALREADY_PROCESSED where LINK = ? ")) {
            statement.setString(1, link);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从数据库中读取URL
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    private static String getNextLink(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    //将新闻页面就存入数据库
    private static void storeIntoDataBaseIfItIsNews(Document doc) {
        ArrayList<Element> titles = doc.select("article");
        if (titles != null) {
            for (Element titleTar : titles) {
                String title = titleTar.child(0).text();
                System.out.println(title);
            }
        }
    }

    private static Document getDocumentbyUrl(String link) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        log.info(link);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity);
        return Jsoup.parse(html);
    }

    private static boolean isInteresting(String link) {
        return "https://sina.cn".equals(link) || link.contains("https://news.sina.cn");
    }
}
