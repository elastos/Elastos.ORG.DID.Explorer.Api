package org.elastos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CacheScheduledTask {
    @Autowired
    ElaDidChainService elaDidChainService;


    private Logger logger = LoggerFactory.getLogger(CacheScheduledTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean onFlag = true;

    public void setOnFlag(boolean onFlag) {
        this.onFlag = onFlag;
    }

    public boolean isOnFlag() {
        return onFlag;
    }



    @Scheduled(fixedDelay = 60 * 1000)
    public void cacheCheckTask() {
        if (!onFlag) {
            return;
        }
        logger.debug("cacheCheckTask begin at:" + dateFormat.format(new Date()));
        elaDidChainService.checkAndRemoveCache();
        logger.debug("cacheCheckTask finish at:" + dateFormat.format(new Date()));
    }
}

