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
import java.util.List;

public class CrawlerMain {
    public static final Log log = LogFactory.getLog(CrawlerMain.class);
    private static final String USESRNAME = "root";
    private static final String PASSWORD = "123456";
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:file:/Users/had/IdeaProjects/sinaCrawler/news",
                USESRNAME, PASSWORD);

        while (true) {
            //从数据库中读取带爬取的路径
            List<String> linkPool = loadUrlsFromDataBase(connection, "select * from LINK_TO_BE_PROCESSED");
            if (linkPool.isEmpty()) {
                break;
            }

            String link = linkPool.remove(linkPool.size() - 1);
            //从数据库中删除删除这次处理过的url
            deleteFromDataBase(connection, link, "delete from LINK_TO_BE_PROCESSED where link = ? ");

            //判断是否是已经处理过的页面
            boolean flag = isProcessed(connection, link);


            if (!flag) {
                //是否是我们感兴趣的页面
                if (isInteresting(link)) {
                    Document doc = getDocumentbyUrl(link);
                    //将新闻页面的href标签的值存入待处理列表
                    for (Element aTarget : doc.select("a")) {
                        String href = aTarget.attr("href");
                        insertLinkIntoDataBase(connection, href, "Insert into LINK_TO_BE_PROCESSED (LINK) VALUES (?)");
                    }
                    //将新闻页面就存入数据库
                    storeIntoDataBaseIfItIsNews(doc);

                    //将分析完的新闻页面放入已处理列表
                    insertLinkIntoDataBase(connection, link, "Insert into LINK_ALREADY_PROCESSED (LINK) VALUES (?)");
                } else {
                    //不感兴趣，暂时不处理它
                }
            }
        }
    }

    private static void insertLinkIntoDataBase(Connection connection, String link, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

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
     * 从数据库中删除链接
     *
     * @param connection
     * @param link
     * @param sql
     * @throws SQLException
     */
    private static void deleteFromDataBase(Connection connection, String link, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

    /**
     * 从数据库中读取URL
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    private static List<String> loadUrlsFromDataBase(Connection connection, String sql) throws SQLException {
        List<String> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
        }
        return list;
    }

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
        log.info(response.getStatusLine());
        log.info("response:" + link);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity);
        return Jsoup.parse(html);
    }

    private static boolean isInteresting(String link) {
        return "https://sina.cn".equals(link) || link.contains("https://news.sina.cn");
    }
}
