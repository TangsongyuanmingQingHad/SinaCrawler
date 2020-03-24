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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawlerMain {
    public static final Log log = LogFactory.getLog(CrawlerMain.class);

    public static void main(String[] args) throws IOException {
        List<String> linkPool = new ArrayList<>();
        Set<String> linkProcessed = new HashSet<>();
        linkPool.add("https://sina.cn");

        while (true) {
            if (linkPool.isEmpty()) {
                break;
            }

            String link = linkPool.remove(linkPool.size() - 1);
            //是否是我们感兴趣的页面
            if (isInteresting(link)) {
                continue;
            }
            //判断是否是已经处理过的页面
            if (linkProcessed.contains(link)) {
                continue;
            }

            Document doc = getDocumentbyUrl(link);
            ArrayList<Element> aTargets = doc.select("a");
            //将新闻页面的href标签的值存入待处理列表
            aTargets.stream().map(aTarget->aTarget.attr("href")).forEach(linkPool::add);
            //将新闻页面就存入数据库
            storeIntoDataBaseIfItIsNews(doc);
            //将分析完的新闻页面放入已处理列表
            linkProcessed.add(link);
        }
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
        return !("https://sina.cn".equals(link) || link.contains("https://news.sina.cn"));
    }
}
