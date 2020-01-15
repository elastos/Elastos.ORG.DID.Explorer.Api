package org.elastos.service;

import org.elastos.entity.ChainDidProperty;
import org.elastos.exception.ElastosServiceException;
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
        //todo  for while height is less than db
        Integer height = elaDidMongoDbService.getCurrentBlockHeight();
        if (null == height) {
            throw  new ElastosServiceException("Err initService elaDidMongoDbService.getCurrentBlockHeight failed");
        }

        mysqlToMongodb();

        onFlag = true;
    }

    private void mysqlToMongodb() {
        List<ChainDidProperty> didProperties;
        do {
            Integer height = elaDidMongoDbService.getCurrentBlockHeight();
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            didProperties = didPropertyOnChainRepository.findFirst100ByHeightGreaterThan(height, sort);
            for (ChainDidProperty property : didProperties) {
                elaDidMongoDbService.setProperty(property);
            }
        } while (!didProperties.isEmpty());
    }

    @Scheduled(fixedDelay =  60 * 1000)
    public void DidDataMysqlToMongodb(){
        if (!onFlag) {
            return;
        }
        mysqlToMongodb();
    }
}
