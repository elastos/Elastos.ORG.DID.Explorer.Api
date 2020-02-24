package org.elastos.service;

import org.elastos.entity.ChainDidProperty;
import org.elastos.repositories.DidPropertyOnChainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SynchronousDataTask {
    private static Logger logger = LoggerFactory.getLogger(SynchronousDataTask.class);

    private boolean onFlag = false;

    @Autowired
    private ElaDidMongoDbService elaDidMongoDbService;

    @Autowired
    private DidPropertyOnChainRepository didPropertyOnChainRepository;

    void initService(){
        mysqlToMongodb();
        onFlag = true;
    }

    private void mysqlToMongodb() {
        List<ChainDidProperty> didProperties;
        do {
            Long id= elaDidMongoDbService.getCurrentDidTableId();
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            didProperties = didPropertyOnChainRepository.findFirst100ByIdGreaterThan(id, sort);
            for (ChainDidProperty property : didProperties) {
                elaDidMongoDbService.setProperty(property);
            }
        } while (!didProperties.isEmpty());
    }

    @Scheduled(fixedDelay =  50 * 1000)
    public void DidDataMysqlToMongodb(){
        if (!onFlag) {
            return;
        }
        mysqlToMongodb();
    }
}
