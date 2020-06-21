package com.tll.elasticsearchdemo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tll
 * @create 2020/6/21 12:16
 * elasticsearch配置
 * spring两步骤
 * 1.找对象
 * 2.交给spring容器管理
 */
@Configuration
public class ElasticsearchClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http")));
        return client;
    }
}
