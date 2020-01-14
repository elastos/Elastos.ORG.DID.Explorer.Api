package org.elastos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitService implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    private ElaDidMongoDbService elaDidMongoDbService;

    @Autowired
    private SynchronousDataTask synchronousDataTask;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("------------In PlatformInitialization----------------");
        elaDidMongoDbService.initService();
        synchronousDataTask.initService();
        logger.info("------------Out PlatformInitialization----------------");
    }
}
