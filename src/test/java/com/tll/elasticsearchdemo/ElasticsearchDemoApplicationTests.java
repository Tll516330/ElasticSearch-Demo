package com.tll.elasticsearchdemo;

import com.alibaba.fastjson.JSON;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.tll.elasticsearchdemo.pojo.User;
import com.tll.elasticsearchdemo.utils.ESconsts;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElasticsearchDemoApplicationTests {

    //面向对象操作
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //测试索引的创建
    @Test
    void testCreateIndex() throws IOException {
        //1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("kuang_index");
        //2、客户端执行请求  返回一个IndicesClient
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        //create(request,RequestOptions.DEFAULT)
        System.out.println(createIndexResponse);
    }

    //测试获取索引
    @Test
    void testGetIndex()throws IOException{
        GetIndexRequest request = new GetIndexRequest("kuang_index");
        boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("kuang_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("王五", 22);
        //创建请求
        IndexRequest request = new IndexRequest("kuang_index");
        //规则 PUT/kuang_index/_doc/1
        request.id("1");
        //连接超时时间
        request.timeout(TimeValue.timeValueSeconds(1));

        //数据放入请求      json
        request.source(JSON.toJSONString(user), XContentType.JSON);

        //客户端发送请求，获取响应结果
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());

    }

    //获取文档是否存在
    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("kuang_index","1");
        //不获取返回的_source 的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("kuang_index", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse);
    }

    //更新文档信息
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("kuang_index", "1");
        updateRequest.timeout("1s");

        User user = new User("张三", 23);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);

        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    //删除文档
    @Test
    void testDelDocument() throws IOException {
        DeleteRequest delete = new DeleteRequest("kuang_index", "1");
        delete.timeout("1s");

        DeleteResponse deleteResponse = client.delete(delete, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    //测试批量插入数据
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("aa",1));
        userList.add(new User("aa",1));
        userList.add(new User("aa",1));
        userList.add(new User("aa",1));


        for (int i = 0; i <userList.size() ; i++) {
            //批量更新和删除在这里就可以实现
            bulkRequest.add(
              new IndexRequest(ESconsts.ES_INDEX)
                    .id(""+i+1)
                    .source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures()); //是否失败
    }

    /**
     * 查询
     * SearchRequest搜索请求
     * SearchSourceBuilder 条件构造
     * TermQueryBuilder精确查询
     * MatchAllQueryBuilder
     * xxxQuery 对应命令
     */
    //批量查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(ESconsts.ES_INDEX);
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件，可以使用QueryBuilders工具实现
        //QueryBuilders.termQuery  精确查询
        //QueryBuilders.matchAllQuery 匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "aa");
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("==========================");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

}
