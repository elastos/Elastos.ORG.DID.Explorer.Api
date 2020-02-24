package org.elastos.repositories;

import com.alibaba.fastjson.JSON;
import org.elastos.entity.ChainBlockHeader;
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
class ChainBlockHeaderRepositoryTest {
    @Autowired
    ChainBlockHeaderRepository chainBlockHeaderRepository;
    @BeforeEach
    void setUp() {
    }

    @Test
    void findTopByOrderByIdDesc() {
        Optional<ChainBlockHeader> headerOp = chainBlockHeaderRepository.findTopByOrderByIdDesc();
        System.out.println(JSON.toJSONString(headerOp.get()));
    }

    @Test
    void findByHeight(){
        List<ChainBlockHeader> headerOp = chainBlockHeaderRepository.findByHeight(21);
        System.out.println(JSON.toJSONString(headerOp));
    }

}