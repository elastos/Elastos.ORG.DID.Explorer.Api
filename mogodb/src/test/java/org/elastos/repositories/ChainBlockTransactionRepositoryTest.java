package org.elastos.repositories;

import com.alibaba.fastjson.JSON;
import org.elastos.entity.ChainBlockTransactionHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class ChainBlockTransactionRepositoryTest {
    @Autowired
    ChainBlockTransactionRepository chainBlockTransactionRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllByHeightAndTxType() {
        List<Map<String, Object>> list =  chainBlockTransactionRepository.findAllByHeight(47959);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    void findDidInfoByHeight() {
        List<Map<String, Object>> list = chainBlockTransactionRepository.findDidInfoByHeight(47959);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    void findValueOfTxid() {
        List<Map<String, Object>> list= chainBlockTransactionRepository.findValueOfTxid("f14051c57f2160b7d9fea10c505c3f8c887381812ac7f3e2f5f4a32fe12fa925");
        System.out.println("*** sum is:" + JSON.toJSONString(list));
    }

    @Test
    void findByTxid() {
        List<Map<String, Object>> obj = chainBlockTransactionRepository.findByTxid("03697fb1650a1916c59b02fe347b7cd65a9a09128b1a67ecc3535c3803b70aaa");
        System.out.println("*** info is:" + JSON.toJSONString(obj));
    }

    @Test
    void findMemoInfoByTxid() {
        List<Map<String, Object>> obj = chainBlockTransactionRepository.findMemoInfoByTxid("03697fb1650a1916c59b02fe347b7cd65a9a09128b1a67ecc3535c3803b70aaa");
        System.out.println("*** memo info is:" + JSON.toJSONString(obj));
    }

}