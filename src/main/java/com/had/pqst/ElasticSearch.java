package com.had.pqst;

import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearch {
    public static void main(String[] args) throws IOException {
        SqlSessionFactory sqlSessionFactory;
        try {
            String resource = "db/mybatis/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<SINANEWS> list = getNewsFromMySql(sqlSessionFactory);
        for (int i = 0; i < 8; i++) {
            new Thread(() -> writeNewsToElasticSearch(list)).start();
        }
    }

    private static void writeNewsToElasticSearch(List<SINANEWS> list) {
//        BulkRequest bulkRequest = new BulkRequest();
        //获取客户端连接
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
            //构建符合ElasticSearch服务器的Http请求
            for (SINANEWS news : list) {
                IndexRequest request = new IndexRequest("sinanews");
                Map<String, Object> map = new HashMap<>();
                map.put("id",news.getId());
                map.put("title", news.getTitle());
                map.put("content", news.getContent().substring(0,10));
                map.put("url", news.getUrl());
                map.put("createAt", news.getCreateAt());
                map.put("modifyAt", news.getModifyAt());
                request.source(map, XContentType.JSON);
                //使用块操作，提升速度
//                bulkRequest.add(request);
                IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
                System.out.println(indexResponse.status().getStatus());
            }
//            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//            System.out.println(bulkResponse.status().getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<SINANEWS> getNewsFromMySql(SqlSessionFactory sqlSessionFactory) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.selectList("com.had.pst.Mock.selectMockNews");
        }
    }
}
