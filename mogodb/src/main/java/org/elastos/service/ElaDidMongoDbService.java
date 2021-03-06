package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        DidDoc didDoc = didInfoCol.findDidInfo(did);
        if ((null != didDoc) && (didDoc.getStatus() == 0)) {
            logger.info("did:" + did + " is deleted");
            return;
        } else {
            didInfoCol.upsertDidInfo(property.getDid(), property.getPublicKey(), property.getDidStatus());
        }

        if (0 != property.getDidStatus()) {
            propertyCol.upsertProperty(property);
        } else {
            propertyCol.delDidProperty(did);
        }
    }

    private void addDidPropertyHistory(String did, String key, Long mysqlId) {
        historyCol.addsertHistory(did, key, mysqlId);
    }

    public void setProperty(ChainDidProperty property) {
        if(MongodbUtil.isChainPropertyBlank(property)){
            return;
        }
        setDidProperty(property);
        addDidPropertyHistory(property.getDid(),
                property.getPropertyKey(), property.getId());
        updateDidTableId(property.getId());
        updateBlockHeight(property.getHeight());
        updateDidTxid(property.getTxid());
    }

    public void delDidList(List<ChainDidProperty> propertyList) {
        if (propertyList.isEmpty()) {
            return;
        }
        List<WriteModel<Document>> request = new ArrayList<>();
        for (ChainDidProperty property : propertyList) {
            WriteModel<Document> doc = propertyCol.delDidPropertyDoc(property.getDid());
            request.add(doc);
        }
        MongodbUtil.bulkWriteUpdate(propertyCol.getCollection(), request);
    }

    public void initByPropertyList(List<ChainDidProperty> propertyList) {
        if (propertyList.isEmpty()) {
            return;
        }
        List<WriteModel<Document>> didRequest = new ArrayList<>();
        List<WriteModel<Document>> propertyRequest = new ArrayList<>();
        List<WriteModel<Document>> historyRequest = new ArrayList<>();

        Integer blockHeight = getCurrentBlockHeight();
        Integer heightCount = getBlockHeightCount();
        String didTxid = getDidTxid();
        Integer txidCount = getDidTxidCount();

        for (ChainDidProperty property : propertyList) {
            if(MongodbUtil.isChainPropertyBlank(property)){
                continue;
            }

            WriteModel<Document> didDoc = didInfoCol.upsertDidInfoDoc(property.getDid(), property.getPublicKey(), property.getDidStatus());
            if (null != didDoc) {
                didRequest.add(didDoc);
            } else {
                continue;
            }
            WriteModel<Document> propertyDoc = propertyCol.upsertPropertyDoc(property);
            if (null != propertyDoc) {
                propertyRequest.add(propertyDoc);
            } else {
                continue;
            }
            WriteModel<Document> historyDoc = historyCol.addsertHistoryDoc(property.getDid(), property.getPropertyKey(), property.getId());
            if (null != historyDoc) {
                historyRequest.add(historyDoc);
            }

            Integer height = property.getHeight();
            if (blockHeight < height) {
                blockHeight = height;
                heightCount++;
            }

            String txid = property.getTxid();
            if (!txid.equals(didTxid)) {
                didTxid = txid;
                txidCount++;
            }
        }
        WriteModel<Document> doc = propertyCol.updateDidTableIdDoc(propertyList.get(propertyList.size() - 1).getId());
        propertyRequest.add(doc);
        doc = propertyCol.updateBlockHeightDoc(blockHeight);
        propertyRequest.add(doc);
        doc = propertyCol.updateBlockHeightCountDoc(heightCount);
        propertyRequest.add(doc);
        doc = propertyCol.updateDidTxidDoc(didTxid);
        propertyRequest.add(doc);
        doc = propertyCol.updateDidTxidCountDoc(txidCount);
        propertyRequest.add(doc);
        try {
            MongodbUtil.bulkWriteUpdate(didInfoCol.getCollection(), didRequest);
            MongodbUtil.bulkWriteUpdate(propertyCol.getCollection(), propertyRequest);
            MongodbUtil.bulkWriteUpdate(historyCol.getCollection(), historyRequest);
        } catch (Exception e) {
            logger.error("initByPropertyList exception:"+ e.getMessage()+ " id:"
                    + propertyList.get(0).getId());
        }

        updateDidTableId(propertyList.get(propertyList.size() - 1).getId());
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

    private ChainDidProperty docToDidProperty(PropertyDoc propertyDoc) {
        ChainDidProperty didProperty = new ChainDidProperty();
        didProperty.setDid(propertyDoc.getDid());
        didProperty.setDidStatus(propertyDoc.getDidStatus());
        didProperty.setPublicKey(propertyDoc.getPublicKey());
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
        RetResult<DidDoc> didDocRetResult = findDidDoc(did);
        if (didDocRetResult.getCode() != RetCode.SUCC) {
            return RetResult.retErr(didDocRetResult.getCode(), didDocRetResult.getMsg());
        }
        List<PropertyDoc> propertyDocs = propertyCol.findAllPropertiesOfDid(did);
        List<ChainDidProperty> chainDidPropertyList = new ArrayList<>();
        for (PropertyDoc propertyDoc : propertyDocs) {
            ChainDidProperty property = docToDidProperty(propertyDoc);
            chainDidPropertyList.add(property);
        }

        return RetResult.retOk(chainDidPropertyList);
    }

    public RetResult<List<ChainDidProperty>> getProperties(String propertyKey) {
        List<PropertyDoc> propertyDocs = propertyCol.findProperties(propertyKey);
        return filterDidPropertyList(propertyDocs);
    }

    public RetResult<List<ChainDidProperty>> getPropertiesLike(String propertyKeyLike) {
        List<PropertyDoc> propertyDocs = propertyCol.findPropertiesLike(propertyKeyLike);
        return filterDidPropertyList(propertyDocs);
    }

    private RetResult<List<ChainDidProperty>> filterDidPropertyList(List<PropertyDoc> propertyDocs) {
        List<ChainDidProperty> chainDidPropertyList = new ArrayList<>();
        for (PropertyDoc propertyDoc : propertyDocs) {
            if (propertyDoc.getDidStatus() == 0) {
                continue;
            }
            ChainDidProperty property = docToDidProperty(propertyDoc);
            chainDidPropertyList.add(property);
        }

        return RetResult.retOk(chainDidPropertyList);
    }

    public RetResult<ChainDidProperty> getProperty(String did, String propertyKey) {
        RetResult<DidDoc> didDocRetResult = findDidDoc(did);
        if (didDocRetResult.getCode() != RetCode.SUCC) {
            return RetResult.retErr(didDocRetResult.getCode(), didDocRetResult.getMsg());
        }

        PropertyDoc propertyDoc = propertyCol.findProperty(did, propertyKey);
        if (null == propertyDoc) {
            return RetResult.retErr(ERROR_DATA_NOT_FOUND, "DID:" + did + " property:" + propertyKey + " not found");
        }

        ChainDidProperty didProperty = docToDidProperty(propertyDoc);
        return RetResult.retOk(didProperty);
    }

    public RetResult<List<ChainDidProperty>> getPropertyLike(String did, String propertyKeyLike) {
        RetResult<DidDoc> didDocRetResult = findDidDoc(did);
        if (didDocRetResult.getCode() != RetCode.SUCC) {
            return RetResult.retErr(didDocRetResult.getCode(), didDocRetResult.getMsg());
        }

        List<PropertyDoc> propertyDocs = propertyCol.findPropertiesLike(did, propertyKeyLike);
        List<ChainDidProperty> chainDidPropertyList = new ArrayList<>();
        for (PropertyDoc propertyDoc : propertyDocs) {
            ChainDidProperty property = docToDidProperty(propertyDoc);
            chainDidPropertyList.add(property);
        }

        return RetResult.retOk(chainDidPropertyList);
    }

    public RetResult<List<Long>> getPropertyHistoryIds(String did, String propertyKey) {
        RetResult<DidDoc> didDocRetResult = findDidDoc(did);
        if (didDocRetResult.getCode() != RetCode.SUCC) {
            return RetResult.retErr(didDocRetResult.getCode(), didDocRetResult.getMsg());
        }

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
        Integer blockHeight = getCurrentBlockHeight();
        if (blockHeight < height) {
            propertyCol.updateBlockHeight(height);
            propertyCol.incBlockHeightCount();
        }
    }

    public Integer getCurrentBlockHeight() {
        return propertyCol.findBlockHeight();
    }

    public Integer getBlockHeightCount() {
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

    public Integer getDidTxidCount() {
        return propertyCol.findDidTxidCount();
    }

    public String getDidSum(){
        long count = didInfoCol.getDidSum();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return JSON.toJSONString(data);
    }

    public String getDidList(int start, int size) {
        List<String> didList = didInfoCol.getDidList(start, size);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String did : didList) {
            Map<String, Object> data = new HashMap<>();
            data.put("did", did);
            result.add(data);
        }
        return JSON.toJSONString(result);
    }

}
