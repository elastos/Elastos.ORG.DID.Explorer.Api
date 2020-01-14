package org.elastos.util;

import com.alibaba.fastjson.JSON;
import org.elastos.conf.MongoDbConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MongodbPropertyHistoryColTest {
    MongodbPropertyHistoryCol propertyHistoryCol = new MongodbPropertyHistoryCol();

    @Autowired
    MongoDbConfiguration mongoDbConfiguration;
    @BeforeEach
    void setUp() {
        propertyHistoryCol.init(MongodbUtil.getCollection("did_property_history"));
    }
    @Test
    void addsertHistory() {
        propertyHistoryCol.addsertHistory("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ", "TestStar1", 3L);
        propertyHistoryCol.addsertHistory("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ", "TestStar1", 4L);
        propertyHistoryCol.addsertHistory("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ", "TestStar1", 5L);
        propertyHistoryCol.addsertHistory("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ", "TestStar1", 6L);
    }

    @Test
    void findHistory() {
       List<Long> ids = propertyHistoryCol.findHistory("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ", "TestStar1");
       System.out.println("ids:"+ JSON.toJSONString(ids));
    }


}