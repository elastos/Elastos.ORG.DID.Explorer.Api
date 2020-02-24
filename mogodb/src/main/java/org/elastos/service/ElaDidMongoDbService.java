package org.elastos.service;

import org.elastos.POJO.DidDoc;
import org.elastos.POJO.PropertyDoc;
import org.elastos.conf.MongoDbConfiguration;
import org.elastos.constant.RetCode;
import org.elastos.entity.ChainDidProperty;
import org.elastos.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.elastos.constant.ServerResponseCode.ERROR_DATA_NOT_FOUND;
import static org.elastos.constant.ServerResponseCode.ERROR_PARAMETER;

@Service
public class ElaDidMongoDbService {
    private static Logger logger = LoggerFactory.getLogger(ElaDidMongoDbService.class);
    private MongodbDidInfoCol didInfoCol = new MongodbDidInfoCol();
    private MongodbPropertyCol propertyCol = new MongodbPropertyCol();
    private MongodbPropertyHistoryCol historyCol = new MongodbPropertyHistoryCol();
    private final String didInfo = "did_info";
    private final String didProperty = "did_property";
    private final String didPropertyHistory = "did_property_history";
    //todo did 和特定property key(可配置) 加入索引

    @Autowired
    MongoDbConfiguration mongoDbConfiguration;

    public void initService() {
        MongodbUtil.init(mongoDbConfiguration.getHost(), mongoDbConfiguration.getPort(), mongoDbConfiguration.getDatabase());

        didInfoCol.init(MongodbUtil.getCollection(didInfo));
        propertyCol.init(MongodbUtil.getCollection(didProperty));
        historyCol.init(MongodbUtil.getCollection(didPropertyHistory));
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
        updateDidTableId(property.getId());
        updateBlockHeight(property.getHeight());
        updateDidTxid(property.getTxid());
    }

    private RetResult<DidDoc> findDidDoc(String did) {
        DidDoc didDoc = didInfoCol.findDidInfo(did);
        if (didDoc == null) {
            logger.info("did:" + did + " is not found");
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " is not found");
        } else if (didDoc.getStatus() == 0) {
            logger.info("did:" + did + " is deleted");
            return RetResult.retErr(ERROR_PARAMETER, "DID:" + did + " is deleted");
        }

        return RetResult.retOk(didDoc);
    }

    private ChainDidProperty docToDidProperty(DidDoc didDoc, PropertyDoc propertyDoc ){
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
        didProperty.setLocalSystemTime(propertyDoc.getLocalSystemTime());
        return didProperty;
    }

    public RetResult<List<ChainDidProperty>> getPropertiesOfDid(String did) {
        RetResult<DidDoc> didDocRetResult = findDidDoc (did);
        if (didDocRetResult.getCode() != RetCode.SUCC) {
            return RetResult.retErr(didDocRetResult.getCode(), didDocRetResult.getMsg());
        }
        DidDoc didDoc = didDocRetResult.getData();
        List < PropertyDoc > propertyDocs = propertyCol.findAllProperties(did);
        List<ChainDidProperty> chainDidPropertyList = new ArrayList<>();
        for (PropertyDoc propertyDoc: propertyDocs) {
            ChainDidProperty property = docToDidProperty(didDoc, propertyDoc);
            chainDidPropertyList.add(property);
        }

        return RetResult.retOk(chainDidPropertyList);
    }

    public RetResult<ChainDidProperty> getProperty(String did, String propertyKey) {
        RetResult<DidDoc> didDocRetResult = findDidDoc (did);
        if (didDocRetResult.getCode() != RetCode.SUCC) {
            return RetResult.retErr(didDocRetResult.getCode(), didDocRetResult.getMsg());
        }
        DidDoc didDoc = didDocRetResult.getData();

        PropertyDoc propertyDoc = propertyCol.findProperty(did, propertyKey);
        if (null == propertyDoc) {
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " property:" + propertyKey + " not found");
        } else if (propertyDoc.getStatus() == 0) {
            logger.info("DID:" + did + " property:" + propertyKey + " is deleted");
            return RetResult.retErr(ERROR_PARAMETER, "DID:" + did + " property:" + propertyKey + " is deleted");
        }

        ChainDidProperty didProperty = docToDidProperty(didDoc, propertyDoc);
        return RetResult.retOk(didProperty);
    }

    public RetResult<List<Long>> getPropertyHistoryIds(String did, String propertyKey) {
        List<Long> ids = historyCol.findHistory(did, propertyKey);
        if (ids.isEmpty()) {
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " property:" + propertyKey + " not found");
        } else {
            return RetResult.retOk(ids);
        }
    }

    private void updateDidTableId(Long id) {
        Long didTableId = propertyCol.findDidTableId();
        if (didTableId < id) {
            propertyCol.updateDidTableId(id);
        }
    }

    public Long getCurrentDidTableId() {
        return propertyCol.findDidTableId();
    }

    private void updateBlockHeight(Integer height) {
        Integer blockHeight  = getCurrentBlockHeight();
        if (blockHeight < height) {
            propertyCol.updateBlockHeight(height);
            propertyCol.incBlockHeightCount();
        }
    }

    public Integer getCurrentBlockHeight() {
        return propertyCol.findBlockHeight();
    }

    public Integer getBlockHeightCount(){
        return propertyCol.findBlockHeightCount();
    }

    private void updateDidTxid(String txid) {
        String didTxid = getDidTxid();
        if (!didTxid.equals(txid)) {
            propertyCol.updateDidTxid(txid);
            propertyCol.incDidTxidCount();
        }
    }

    public String getDidTxid() {
        return propertyCol.findDidTxid();
    }

    public Integer getDidTxidCount(){
        return propertyCol.findDidTxidCount();
    }

}
