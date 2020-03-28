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
import java.util.stream.Collectors;

public class CrawlerMain extends Thread {
    private JdbcCrawlerDao dao;
    private static Log log;

    public CrawlerMain(JdbcCrawlerDao dao, Log log) {
        this.dao = dao;
        this.log = log;
    }


    @Override
    public void run() {
        try {
            String link;
            //从数据库中加载下一个要爬取的路径
            while ((link = dao.getLinkThenDelete()) != null) {
                if (dao.isProcessed(link)) {
                    continue;
                }
                //是否是我们感兴趣的页面
                if (isInteresting(link)) {
                    Document doc = getDocumentbyUrl(link);
                    parseUrlsFromPagesIntoDataBase(doc);
                    storeIntoDataBaseIfItIsNews(doc, link);
                    dao.insertLinkIntoAlreadyDataBase(link);
                } else {
                    //不感兴趣，暂时不处理它
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //将新闻页面的href标签的值存入待处理列表
    private void parseUrlsFromPagesIntoDataBase(Document doc) throws SQLException {
        for (Element aTarget : doc.select("a")) {
            String href = aTarget.attr("href");
            if (href.startsWith("//")) {
                href = "https://" + href;
            }

            if (href.startsWith("http")) {
                dao.insertLinkIntoDataBase(href);
            }
        }
    }


    //将新闻页面就存入数据库
    private void storeIntoDataBaseIfItIsNews(Document doc, String link) throws SQLException {
        ArrayList<Element> titles = doc.select("article");
        if (titles != null) {
            for (Element titleTar : titles) {
                String title = titleTar.child(0).text();
                log.info("开始插入数据：" + title);
                String content = titleTar.select("p").stream().map(Element::text).collect(Collectors.joining("/n"));
                dao.insertNewsIntoDataBase(title, content, link);
                log.info("插入数据成功");
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
        return "https://sina.cn".equals(link) || link.startsWith("https://news.sina.cn");
    }
}
