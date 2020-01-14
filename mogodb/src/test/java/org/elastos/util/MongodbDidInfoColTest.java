package org.elastos.util;

import com.alibaba.fastjson.JSON;
import org.elastos.POJO.DidDoc;
import org.elastos.conf.MongoDbConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MongodbDidInfoColTest {

    MongodbDidInfoCol mongodbDidInfoCol = new MongodbDidInfoCol();

    @Autowired
    MongoDbConfiguration mongoDbConfiguration;

    @BeforeEach
    void setUp() {
        mongodbDidInfoCol.init(MongodbUtil.getCollection("did_info"));
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void upsertDidInfo() {
        mongodbDidInfoCol.upsertDidInfo("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ",
                "02600A8B3B10DE8F3AA7FC646C6A9A760AB8E3114B8333B6F2873C6BF9746827EE",
                1);
    }

    @Test
    void findDidInfo() {
        DidDoc didDoc = mongodbDidInfoCol.findDidInfo("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ");
        System.out.println("did:"+ JSON.toJSONString(didDoc));
    }

}