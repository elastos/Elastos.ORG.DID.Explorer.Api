package org.elastos.repositories;

import org.elastos.entity.ChainDidProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChainDidPropertyRepositoryTest {
    @Autowired
    DidPropertyOnChainRepository didPropertyOnChainRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByDid() throws Exception {
        Sort sort = Sort.by(Sort.Direction.DESC, "blockTime");
        List<ChainDidProperty> properties
                = didPropertyOnChainRepository.findByDid(
                "ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", sort);
        System.out.println("Property data size" + properties.size());
    }

}