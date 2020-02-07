package org.elastos.repositories;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DidPropertyOnChainRepositoryTest {
    @Autowired
    DidPropertyOnChainRepository didPropertyOnChainRepository;
    @Test
    public void findGroupByTxid() throws Exception {
        List<String> txids = didPropertyOnChainRepository.findGroupByTxid();
        System.out.println(JSON.toJSONString(txids));
    }

    @Test
    public void deleteByTxid() throws Exception {
        didPropertyOnChainRepository.deleteByTxid("2497ed04398c3f3f1a4cd8ad62d50bd617920d4e355e3efe34de3dc8d7107f1a");
    }

}