package org.elastos.service;

import org.elastos.POJO.DidDoc;
import org.elastos.POJO.PropertyDoc;
import org.elastos.conf.MongoDbConfiguration;
import org.elastos.entity.ChainDidProperty;
import org.elastos.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.elastos.constant.ServerResponseCode.ERROR_DATA_NOT_FOUND;
import static org.elastos.constant.ServerResponseCode.ERROR_PARAMETER;

@Service
public class ElaDidMongoDbService {
    private static Logger logger = LoggerFactory.getLogger(SynchronousDataTask.class);
    private MongodbDidInfoCol didInfoCol = new MongodbDidInfoCol();
    private MongodbPropertyCol propertyCol = new MongodbPropertyCol();
    private MongodbPropertyHistoryCol historyCol = new MongodbPropertyHistoryCol();
    private Integer blockHeight = 0;
    private final String didInfo = "did_info";
    private final String didProperty = "did_property";
    private final String didPropertyHistory = "did_property_history";

    @Autowired
    MongoDbConfiguration mongoDbConfiguration;

    public void initService() {
        MongodbUtil.init(mongoDbConfiguration.getHost(), mongoDbConfiguration.getPort(), mongoDbConfiguration.getDatabase());

        didInfoCol.init(MongodbUtil.getCollection(didInfo));
        propertyCol.init(MongodbUtil.getCollection(didProperty));
        historyCol.init(MongodbUtil.getCollection(didPropertyHistory));

        blockHeight = propertyCol.findBlockHeight();
    }


    private void setDidProperty(ChainDidProperty property) {
        //did信息单独存document。 一个property也单独存一个document，因为一个区块8m，所以不会发生document不够大的情况
        String did = property.getDid();
        String key = property.getPropertyKey();

        DidDoc didDoc = didInfoCol.findDidInfo(did);
        if ((null != didDoc) && (didDoc.getStatus() == 0)) {
            logger.info("did:" + did + " is deleted");
            return;
        } else {
            didInfoCol.upsertDidInfo(property.getDid(), property.getPublicKey(), property.getDidStatus());
        }

        PropertyDoc propertyDoc = propertyCol.findProperty(did, key);
        if ((null != propertyDoc) && (propertyDoc.getStatus() == 0)) {
            logger.info("did:" + did + " " + key + " is deleted");
        } else {
            propertyCol.upsertProperty(property);
        }
    }

    private void addDidPropertyHistory(String did, String key, Long mysqlId) {
        historyCol.addsertHistory(did, key, mysqlId);
    }


    public void setProperty(ChainDidProperty property) {
        setDidProperty(property);
        addDidPropertyHistory(property.getDid(),
                property.getPropertyKey(), property.getId());
        updateBlockHeight(property.getHeight());
    }

    public RetResult<ChainDidProperty> getProperty(String did, String propertyKey) {
        DidDoc didDoc = didInfoCol.findDidInfo(did);
        if (didDoc == null) {
            logger.info("did:" + did + " is not found");
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " is not found");
        } else if (didDoc.getStatus() == 0) {
            logger.info("did:" + did + " is deleted");
            return RetResult.retErr(ERROR_PARAMETER, "DID:" + did + " is deleted");
        }

        PropertyDoc propertyDoc = propertyCol.findProperty(did, propertyKey);
        if (null == propertyDoc) {
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " property:" + propertyKey + " not found");
        } else if (propertyDoc.getStatus() == 0) {
            logger.info("DID:" + did + " property:" + propertyKey + " is deleted");
            return RetResult.retErr(ERROR_PARAMETER, "DID:" + did + " property:" + propertyKey + " is deleted");
        }

        ChainDidProperty didProperty = new ChainDidProperty();
        didProperty.setDid(didDoc.getDid());
        didProperty.setDidStatus(didDoc.getStatus());
        didProperty.setPublicKey(didDoc.getPublicKey());
        didProperty.setId(propertyDoc.getId());
        didProperty.setPropertyStatus(propertyDoc.getStatus());
        didProperty.setPropertyKey(propertyDoc.getPropertyKey());
        didProperty.setPropertyValue(propertyDoc.getPropertyValue());
        didProperty.setTxid(propertyDoc.getTxid());
        didProperty.setBlockTime(propertyDoc.getBlockTime());
        didProperty.setHeight(propertyDoc.getHeight());

        return RetResult.retOk(didProperty);
    }

    public RetResult<List<Long>> getPropertyHistory(String did, String propertyKey) {
        List<Long> ids = historyCol.findHistory(did, propertyKey);
        if (ids.isEmpty()) {
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " property:" + propertyKey + " not found");
        } else {
            return RetResult.retOk(ids);
        }
    }

    private void updateBlockHeight(Integer height) {
        if (blockHeight < height) {
            propertyCol.updateBlockHeight(height);
            blockHeight = height;
        }
    }

    public Integer getCurrentBlockHeight() {
        return blockHeight;
    }
}
