package org.elastos.util;

import com.alibaba.fastjson.JSON;
import org.elastos.POJO.PropertyDoc;
import org.elastos.conf.MongoDbConfiguration;
import org.elastos.entity.ChainDidProperty;
import org.elastos.repositories.DidPropertyOnChainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
class MongodbPropertyColTest {
    MongodbPropertyCol propertyCol = new MongodbPropertyCol();

    @Autowired
    MongoDbConfiguration mongoDbConfiguration;

    @Autowired
    DidPropertyOnChainRepository propertyOnChainRepository;

    @BeforeEach
    void setUp() {
        propertyCol.init(MongodbUtil.getCollection("did_property"));
    }

    @Test
    void updateBlockHeight() {
        propertyCol.updateBlockHeight(100);
    }

    @Test
    void findBlockHeight() {
        Integer height = propertyCol.findBlockHeight();
    }
    @Test
    void upsertProperty() {
        Optional<ChainDidProperty> propertyOp = propertyOnChainRepository.findById(3L);
        if (!propertyOp.isPresent()) {
            System.out.println("Err upsertProperty");
            return;
        }

        ChainDidProperty property = propertyOp.get();

        propertyCol.upsertProperty(property);

        property.setBlockTime(1548682163);
        property.setTxid("2497ed043e8c3f3f1a4cd8ad62d50bd617920d4e355e3efe34de3dc8d7107f1a");
        property.setHeight(47959);

        propertyCol.upsertProperty(property);

        property.setPropertyKey("TestStar2");
        property.setBlockTime(1548672163);
        property.setTxid("2497ed043e8c3f3f1a4cd8ad62d50bd617920d4e355e3efe34de3dc8d7107fee");
        property.setHeight(47956);

        propertyCol.upsertProperty(property);
    }

    @Test
    void findProperty() {
       PropertyDoc propertyDoc =  propertyCol.findProperty("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ", "TestStar1");
       System.out.println("property:" + JSON.toJSONString(propertyDoc));
    }

    @Test
    void findAllProperties() {
        List<PropertyDoc> propertyDocs =  propertyCol.findAllProperties("iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ");
        System.out.println("property list:" + JSON.toJSONString(propertyDocs));
    }


}