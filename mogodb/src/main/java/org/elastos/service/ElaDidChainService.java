package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.elastos.POJO.DidEntity;
import org.elastos.POJO.PropertiesOfDidApp;
import org.elastos.constant.RetCode;
import org.elastos.constant.ServerResponseCode;
import org.elastos.entity.CacheDidProperty;
import org.elastos.entity.ChainDidApp;
import org.elastos.entity.ChainDidProperty;
import org.elastos.repositories.DidAppOnChainRepository;
import org.elastos.repositories.DidPropertyOnCacheRepository;
import org.elastos.repositories.DidPropertyOnChainRepository;
import org.elastos.util.ListPage;
import org.elastos.util.RetResult;
import org.elastos.util.ServerResponse;
import org.elastos.util.ela.ElaKit;
import org.elastos.util.ela.ElaSignTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElaDidChainService {
    private static Logger logger = LoggerFactory.getLogger(ElaDidChainService.class);

    @Autowired
    DidPropertyOnChainRepository didPropertyOnChainRepository;

    @Autowired
    private DidPropertyOnCacheRepository didPropertyOnCacheRepository;

    @Autowired
    private DidAppOnChainRepository didAppOnChainRepository;

    @Autowired
    ElaDidMongoDbService elaDidMongoDbService;

    private <T> List<T> ListPage(List<T> properties, Integer page, Integer size) {
        if ((null == page) || (null == size)) {
            return properties;
        } else {
            return ListPage.getListPage(properties, page, size);
        }
    }

    public String getDidProperties(String did, Integer page, Integer size) {
        RetResult<List<ChainDidProperty>> ret = elaDidMongoDbService.getPropertiesOfDid(did);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        List<ChainDidProperty> didProperties = ret.getData();

        return ServerResponse.retOk(ListPage(didProperties, page, size));
    }

    public String getDidProperty(String did, String propertyKey) {
        RetResult<ChainDidProperty> ret = elaDidMongoDbService.getProperty(did, propertyKey);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        ChainDidProperty didProperty = ret.getData();
        return ServerResponse.retOk(didProperty);
    }

    public String getDidPropertyHistory(String did, String propertyKey, Integer page, Integer size) {
        RetResult<List<Long>> ret = elaDidMongoDbService.getPropertyHistoryIds(did, propertyKey);
        if ((ret.getCode() != RetCode.SUCC) ||(ret.getData().isEmpty())) {
            return ServerResponse.retOk("");
        }
        List<ChainDidProperty> properties = didPropertyOnChainRepository.findAllById(ret.getData());
        return ServerResponse.retOk(ListPage(properties, page, size));
    }

    public String getDIDPropertyHistoryCount(String did, String propertyKey) {
        RetResult<List<Long>> ret = elaDidMongoDbService.getPropertyHistoryIds(did, propertyKey);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        } else {
            int count = ret.getData().size();
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            List<Map<String, Object>> r = new ArrayList<>();
            r.add(data);
            return JSON.toJSONString(r);
        }
    }

    public String getDIDPropertyLike(String did, String propertyKeyLike, Integer page, Integer size) {
        RetResult<List<ChainDidProperty>> ret = elaDidMongoDbService.getPropertyLike(did, propertyKeyLike);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        List<ChainDidProperty> didProperties = ret.getData();

        return ServerResponse.retOk(ListPage(didProperties, page, size));
    }

    public String getDIDPropertyHistoryLike(String did, String propertyKeyLike, Integer size, Integer page) {
        RetResult<List<ChainDidProperty>> ret = elaDidMongoDbService.getPropertyLike(did, propertyKeyLike);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        List<ChainDidProperty> didProperties = ret.getData();

        List<ChainDidProperty> returnProperties = new ArrayList<>();
        for (ChainDidProperty chainDidProperty : didProperties) {
            RetResult<List<Long>> idsRet = elaDidMongoDbService.getPropertyHistoryIds(did, chainDidProperty.getPropertyKey());
            if ((idsRet.getCode() != RetCode.SUCC) ||(idsRet.getData().isEmpty())) {
                continue;
            }
            List<ChainDidProperty> properties = didPropertyOnChainRepository.findAllById(idsRet.getData());
            returnProperties.addAll(properties);
        }

        return ServerResponse.retOk(ListPage(returnProperties, page, size));
    }

    public String getPropertiesCrossDID(String propertyKey, Integer page, Integer size) {
        RetResult<List<ChainDidProperty>> ret = elaDidMongoDbService.getProperties(propertyKey);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        List<ChainDidProperty> didProperties = ret.getData();
        return ServerResponse.retOk(ListPage(didProperties, page, size));
    }

    public String getPropertiesCrossDidCount(String propertyKey) {
        RetResult<List<ChainDidProperty>> ret = elaDidMongoDbService.getProperties(propertyKey);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        int count =  ret.getData().size();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        List<Map<String, Object>> r = new ArrayList<>();
        r.add(data);
        return JSON.toJSONString(r);
    }

    public String getPropertiesLikeCrossDID(String propertyKeyLike, Integer page, Integer size) {
        RetResult<List<ChainDidProperty>> ret = elaDidMongoDbService.getPropertiesLike(propertyKeyLike);
        if (ret.getCode() != RetCode.SUCC) {
            return ServerResponse.retOk("");
        }

        List<ChainDidProperty> didProperties = ret.getData();
        return ServerResponse.retOk(ListPage(didProperties, page, size));
    }

    public String getPropertiesOfAppId(String appId) {

        Sort sort = Sort.by(Sort.Direction.DESC, "blockTime", "id");

        List<ChainDidApp> onChainDidApp = didAppOnChainRepository.findByInfoValue(appId, sort);
        if ((null == onChainDidApp) || (onChainDidApp.isEmpty())) {
            logger.debug("getPropertiesOfAppId There is no data in database. appid= {}", appId);
            return ServerResponse.retOk("");
        }

        PropertiesOfDidApp propertiesOfDidApp = new PropertiesOfDidApp();
        propertiesOfDidApp.setDid(onChainDidApp.get(0).getDid());
        propertiesOfDidApp.setPublic_key(onChainDidApp.get(0).getPublicKey());
        propertiesOfDidApp.setApp_id(onChainDidApp.get(0).getInfoValue());

        return ServerResponse.retOk(propertiesOfDidApp);
    }

    public String setPropertyCache(String raw, String txid) {
        JSONObject rawData = JSON.parseObject(raw);

        String hexMsg = rawData.getString("msg");
        String publicKey = rawData.getString("pub");
        String sig = rawData.getString("sig");
        boolean verifyRet = verify(publicKey, sig, hexMsg);
        if (!verifyRet) {
            return ServerResponse.retErr(ServerResponseCode.VERIFY_ERROR, "Verify raw data failed");
        }
        String did = ElaKit.getIdentityFromPublicKey(publicKey);
        String msg = new String(DatatypeConverter.parseHexBinary(hexMsg));
        DidEntity didEntity = JSON.parseObject(msg, DidEntity.class);
        String tag = didEntity.getTag();
        if (StringUtil.isBlank(tag) || !DidEntity.DID_TAG.equals(tag)) {
            return ServerResponse.retErr(ServerResponseCode.VERIFY_DID_ERROR, "Verify did entity failed");
        }
        didEntity.setDid(did);

        List<CacheDidProperty> cacheDidProperties = new ArrayList<>();

        List<DidEntity.DidProperty> properties = didEntity.getProperties();
        for (DidEntity.DidProperty property : properties) {
            CacheDidProperty cacheDidProperty = new CacheDidProperty();
            cacheDidProperty.setDid(did);
            cacheDidProperty.setDidStatus(didEntity.getStatus());
            cacheDidProperty.setPublicKey(publicKey);
            cacheDidProperty.setTxid(txid);
            cacheDidProperty.setKey(property.getKey());
            cacheDidProperty.setValue(property.getValue());
            cacheDidProperty.setStatus(property.getStatus());

            if(!StringUtils.isAnyBlank(cacheDidProperty.getDid(),
                    cacheDidProperty.getDidStatus(),
                    cacheDidProperty.getKey(),
                    cacheDidProperty.getValue(),
                    cacheDidProperty.getStatus())){
                cacheDidProperties.add(cacheDidProperty);
            }
        }

        if (!cacheDidProperties.isEmpty()) {
            didPropertyOnCacheRepository.saveAll(cacheDidProperties);
        }

        return ServerResponse.retOk("");
    }

    private static boolean verify(String hexPub, String hexSig, String hexMsg) {
        byte[] msg = DatatypeConverter.parseHexBinary(hexMsg);
        byte[] sig = DatatypeConverter.parseHexBinary(hexSig);
        byte[] pub = DatatypeConverter.parseHexBinary(hexPub);
        boolean isVerify = ElaSignTool.verify(msg, sig, pub);
        return isVerify;
    }

    public String getPropertiesOfDidFromCache(String did) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime", "id");
        List<CacheDidProperty> cacheDidProperties = didPropertyOnCacheRepository.findAllByDid(did, sort);
        if ((null == cacheDidProperties) || (cacheDidProperties.isEmpty())) {
            logger.debug("getPropertyHistoryFromCache There is no data in database. did = {}", did);
            return ServerResponse.retOk("");
        }
        return ServerResponse.retOk(cacheDidProperties);
    }

    public String getDIDPropertyFromCache(String did, String propertyKey) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime", "id");
        List<CacheDidProperty> cacheDidProperties = didPropertyOnCacheRepository.findFirstByDidAndAndKey(did, propertyKey, sort);
        if (cacheDidProperties.isEmpty()) {
            logger.debug("getPropertyHistoryFromCache There is no data in database. did = {},propertyKey={}", did, propertyKey);
            return ServerResponse.retOk("");
        }
        return ServerResponse.retOk(cacheDidProperties);
    }

    public String getPropertyHistoryFromCache(String did, String propertyKey) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime", "id");
        List<CacheDidProperty> cacheDidProperties = didPropertyOnCacheRepository.findAllByDidAndAndKey(did, propertyKey, sort);
        if ((null == cacheDidProperties) || (cacheDidProperties.isEmpty())) {
            logger.debug("getPropertyHistoryFromCache There is no data in database. did = {},propertyKey={}", did, propertyKey);
            return ServerResponse.retOk("");
        }
        return ServerResponse.retOk(cacheDidProperties);
    }

    void checkAndRemoveCache() {
        List<String> txidList = didPropertyOnCacheRepository.findGroupByTxid();
        if (txidList.isEmpty()) {
            return;
        }
        for (String txid : txidList) {
            List<ChainDidProperty> properties = didPropertyOnChainRepository.findAllByTxid(txid);
            if (!properties.isEmpty()) {
                didPropertyOnCacheRepository.deleteByTxid(txid);
            }
        }
    }
}
