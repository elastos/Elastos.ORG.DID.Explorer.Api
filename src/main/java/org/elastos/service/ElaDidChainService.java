package org.elastos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.elastos.POJO.*;
import org.elastos.conf.NodeConfiguration;
import org.elastos.conf.RetCodeConfiguration;
import org.elastos.constant.RetCode;
import org.elastos.entity.*;
import org.elastos.repositories.DidAppOnChainRepository;
import org.elastos.repositories.DidPropertyOnCacheRepository;
import org.elastos.repositories.DidPropertyOnChainRepository;
import org.elastos.util.RetResult;
import org.elastos.util.ela.ElaKit;
import org.elastos.util.ela.ElaSignTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

import static org.elastos.POJO.InputDidStatus.normal;


@Service
public class ElaDidChainService {

    @Autowired
    private RetCodeConfiguration retCodeConfiguration;

    @Autowired
    private DidPropertyOnChainRepository didPropertyOnChainRepository;

    @Autowired
    private DidPropertyOnCacheRepository didPropertyOnCacheRepository;

    @Autowired
    private DidAppOnChainRepository didAppOnChainRepository;

    @Autowired
    private NodeConfiguration nodeConfiguration;

    private static Logger logger = LoggerFactory.getLogger(ElaDidChainService.class);

    public ReturnMsgEntity getDetailedPropertiesOfDid(String did, InputDidStatus status, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");

        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDetailedPropertiesOfDid There is no data in database. did = {},status={}", did, status);
            System.out.println("getDetailedPropertiesOfDid There is no data in database. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        Set<ChainDetailedDidProperty> propertySet = new HashSet<>();
        if (!filterValidDidDetailedProperties(onChainProperties, null, propertySet)) {
            logger.debug("getDetailedPropertiesOfDid The did is deprecated. did = {},status={}", did, status);
            System.out.println("getDetailedPropertiesOfDid The did is deprecated. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        if (status == normal) {
            propertySet.removeIf(property -> property.getPropertyStatus().equals("0"));
        } else if (status == InputDidStatus.deprecated) {
            propertySet.removeIf(property -> property.getPropertyStatus().equals("1"));
        }

        if (!propertySet.isEmpty()) {
            String ret = detailedPropertiesToJson(new ArrayList<>(propertySet), page, size);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getDetailedPropertiesOfDid There is no property data . did = {},status={}", did, status);
            System.out.println("getDetailedPropertiesOfDid There is no property data. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getPropertiesOfDid(String did, InputDidStatus status, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");

        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getPropertiesOfDid There is no data in database. did = {},status={}", did, status);
            System.out.println("getPropertiesOfDid There is no data in database. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        Set<DidProperty> propertySet = new HashSet<>();
        if (!filterValidDidProperties(onChainProperties, null, propertySet)) {
            logger.debug("getPropertiesOfDid The did is deprecated. did = {},status={}", did, status);
            System.out.println("getPropertiesOfDid The did is deprecated. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        if (status == normal) {
            propertySet.removeIf(property -> property.getStatus() == InputDidStatus.deprecated);
        } else if (status == InputDidStatus.deprecated) {
            propertySet.removeIf(property -> property.getStatus() == normal);
        }


        if (!propertySet.isEmpty()) {
            String ret = propertiesToJson(new ArrayList<>(propertySet), page, size);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getPropertiesOfDid There is no property data . did = {},status={}", did, status);
            System.out.println("getPropertiesOfDid There is no property data. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDProperty(String did, String propertyKey) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");
        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDProperty There is no data in database. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDProperty There is no data in database. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        //Use database descendant list and Set filter the latest data in property.
        Set<DidProperty> propertySet = new HashSet<>();
        if (!filterValidDidProperties(onChainProperties, propertyKey, propertySet)) {
            logger.debug("getDIDProperty The did is deprecated. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDProperty The did is deprecated. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        propertySet.removeIf(property -> property.getStatus() == InputDidStatus.deprecated);

        if (!propertySet.isEmpty()) {
            String ret = propertiesToJson(new ArrayList<>(propertySet), null, null);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getDIDProperty There is no property data. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDProperty There is no property data. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDPropertyHistory(String did, String propertyKey, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");
        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDPropertyHistory There is no data in database. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistory getDIDProperty There is no data in database. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        List<DidProperty> properties = new ArrayList<>();
        if (!filterValidDidProperties(onChainProperties, propertyKey, properties)) {
            logger.debug("getDIDPropertyHistory The did is deprecated. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistory The did is deprecated. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
        if (!properties.isEmpty()) {
            String ret = propertiesToJson(properties, page, size);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getDIDPropertyHistory There is no property data . did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistory There is no property data. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDPropertyLike(String did, String propertyKey, Integer blockHeightMin) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");
        List<ChainDidProperty> onChainProperties;
        if (null == blockHeightMin) {
            onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        } else {
            onChainProperties = didPropertyOnChainRepository.findByDidAndHeightIsGreaterThanEqual(did, blockHeightMin, sort);
        }
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDPropertyLike There is no data in database. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyLike There is no data in database. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        //Use database descendant list and Set filter the latest data in property.
        Set<DidProperty> propertySet = new HashSet<>();
        if (!filterValidDidPropertiesLike(onChainProperties, propertyKey, propertySet)) {
            logger.debug("getDIDPropertyLike The did is deprecated. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyLike The did is deprecated. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        propertySet.removeIf(property -> property.getStatus() == InputDidStatus.deprecated);

        if (!propertySet.isEmpty()) {
            String ret = propertiesToJson(new ArrayList<>(propertySet), null, null);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getDIDPropertyLike There is no property data. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyLike There is no property data. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDPropertyHistoryLike(String did, String propertyKey, Integer blockHeightMin, Integer size, Integer page) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");
        List<ChainDidProperty> onChainProperties;
        if (null == blockHeightMin) {
            onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        } else {
            onChainProperties = didPropertyOnChainRepository.findByDidAndHeightIsGreaterThanEqual(did, blockHeightMin, sort);
        }

        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDPropertyHistoryLike There is no data in database. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistoryLike getDIDProperty There is no data in database. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        List<DidProperty> properties = new ArrayList<>();
        if (!filterValidDidPropertiesLike(onChainProperties, propertyKey, properties)) {
            logger.debug("getDIDPropertyHistoryLike The did is deprecated. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistoryLike The did is deprecated. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
        if (!properties.isEmpty()) {
            String ret = propertiesToJson(properties, page, size);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getDIDPropertyHistoryLike There is no property data . did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistoryLike There is no property data. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    private String propertiesToJson(List<DidProperty> properties, Integer page, Integer size) {
        if (null == page) {
            return JSON.toJSONString(properties);
        } else {
            Pageable pageable = PageRequest.of(page, (null == size) ? 20 : size);
            int start = (int) pageable.getOffset();
            int end = (start + pageable.getPageSize()) > properties.size() ? properties.size() : (start + pageable.getPageSize());
            List<DidProperty> subList = properties.subList(start, end);
            return JSON.toJSONString(subList);
        }
    }

    private String detailedPropertiesToJson(List<ChainDetailedDidProperty> properties, Integer page, Integer size) {
        properties.forEach(pro -> pro.setPropertyStatus(pro.getPropertyStatus().equals("1") ? DidStatus.Normal.toString() : DidStatus.Deprecated.toString()));
        properties.forEach(pro -> pro.setDidStatus(pro.getDidStatus().equals("1") ? DidStatus.Normal.toString() : DidStatus.Deprecated.toString()));
        if (null == page) {
            return JSON.toJSONString(properties);
        } else {
            Pageable pageable = PageRequest.of(page, (null == size) ? 20 : size);
            int start = (int) pageable.getOffset();
            int end = (start + pageable.getPageSize()) > properties.size() ? properties.size() : (start + pageable.getPageSize());
            List<ChainDetailedDidProperty> subList = properties.subList(start, end);
            return JSON.toJSONString(subList);
        }
    }

    private boolean filterValidDidDetailedProperties(List<ChainDidProperty> propertyDescList, String propertyKey, Collection<ChainDetailedDidProperty> properties) {
        //Every time has to
        for (ChainDidProperty p : propertyDescList) {
            if ("1".equals(p.getDidStatus())) {
                //If propertyKey is null, means get all properties.
                if (null == propertyKey) {
                    ChainDetailedDidProperty pro = new ChainDetailedDidProperty(p);
                    properties.add(pro);
                } else {
                    //If has propertyKey, we filter the just property.
                    if (propertyKey.equals(p.getPropertyKey())) {
                        ChainDetailedDidProperty pro = new ChainDetailedDidProperty(p);
                        properties.add(pro);
                    }
                }
            } else {
                //The did has been deprecated.
                return false;
            }
        }
        return true;
    }

    private boolean filterValidDidPropertiesLike(List<ChainDidProperty> propertyDescList, String propertyKey, Collection<DidProperty> properties) {
        //Every time has to
        for (ChainDidProperty p : propertyDescList) {
            if ("1".equals(p.getDidStatus())) {
                if (p.getPropertyKey().contains(propertyKey)) {
                    saveProperty(properties, p);
                }
            } else {
                //The did has been deprecated.
                return false;
            }
        }
        return true;
    }

    private boolean filterValidDidProperties(List<ChainDidProperty> propertyDescList, String propertyKey, Collection<DidProperty> properties) {
        //Every time has to
        for (ChainDidProperty p : propertyDescList) {
            if ("1".equals(p.getDidStatus())) {
                //If propertyKey is null, means get all properties.
                if (null == propertyKey) {
                    saveProperty(properties, p);
                } else {
                    //If has propertyKey, we filter the just property.
                    if (propertyKey.equals(p.getPropertyKey())) {
                        saveProperty(properties, p);
                    }
                }
            } else {
                //The did has been deprecated.
                return false;
            }
        }
        return true;
    }

    private void saveProperty(Collection<DidProperty> properties, ChainDidProperty p) {
        DidProperty didProperty = new DidProperty();
        didProperty.saveToDidProperty(p);
        properties.add(didProperty);
    }

    public ReturnMsgEntity getPropertiesCrossDID(String propertyKey, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");

        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByPropertyKey(propertyKey, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getPropertiesCrossDID There is no data in database. property= {}", propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        Set<PropertyOfDid> propertySet = new HashSet<>();
        if (!filterValidPropertiesOfDid(onChainProperties, propertySet)) {
            logger.debug("getPropertiesOfDid The did is deprecated. property = {}", propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        //We only took the valid
        propertySet.removeIf(property -> property.getStatus() == InputDidStatus.deprecated);

        if (!propertySet.isEmpty()) {
            String ret = propertiesOfDidToJson(new ArrayList<>(propertySet), page, size);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getPropertiesCrossDID There is no property data . property = {}", propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    private boolean filterValidPropertiesOfDid(List<ChainDidProperty> propertyDescList, Collection<PropertyOfDid> properties) {
        //Every time has to
        for (ChainDidProperty p : propertyDescList) {
            if ("1".equals(p.getDidStatus())) {
                savePropertyOfDid(properties, p);
            }
        }
        return true;
    }

    private void savePropertyOfDid(Collection<PropertyOfDid> properties, ChainDidProperty p) {
        PropertyOfDid didProperty = new PropertyOfDid();
        didProperty.setDid(p.getDid());
        didProperty.setKey(p.getPropertyKey());
        didProperty.setValue(p.getPropertyValue());
        //There is more than one property data for a did. we only took the latest one.
        if ("1".equals(p.getPropertyStatus())) {
            didProperty.setStatus(InputDidStatus.normal);
        } else {
            didProperty.setStatus(InputDidStatus.deprecated);
        }
        properties.add(didProperty);
    }

    private String propertiesOfDidToJson(List<PropertyOfDid> properties, Integer page, Integer size) {
        //clean status, do not output it for user.
        properties.forEach(pro -> pro.setStatus(null));
        if (null == page) {
            return JSON.toJSONString(properties);
        } else {
            Pageable pageable = PageRequest.of(page, (null == size) ? 20 : size);
            int start = (int) pageable.getOffset();
            int end = (start + pageable.getPageSize()) > properties.size() ? properties.size() : (start + pageable.getPageSize());
            List<PropertyOfDid> subList = properties.subList(start, end);
            return JSON.toJSONString(subList);
        }
    }

    public ReturnMsgEntity getPropertiesOfAppId(String appId) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "id");

        List<ChainDidApp> onChainDidApp = didAppOnChainRepository.findByInfoValue(appId, sort);
        if ((null == onChainDidApp) || (onChainDidApp.isEmpty())) {
            logger.debug("getPropertiesOfAppId There is no data in database. appid= {}", appId);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        PropertiesOfDidApp propertiesOfDidApp = new PropertiesOfDidApp();
        propertiesOfDidApp.setDid(onChainDidApp.get(0).getDid());
        propertiesOfDidApp.setPublic_key(onChainDidApp.get(0).getPublicKey());
        propertiesOfDidApp.setApp_id(onChainDidApp.get(0).getInfoValue());

        return new ReturnMsgEntity().setResult(JSON.toJSONString(propertiesOfDidApp)).setStatus(retCodeConfiguration.SUCC());
    }

    public ReturnMsgEntity setPropertyCache(String raw, String txid) {
        JSONObject rawData = JSON.parseObject(raw);

        String hexMsg = rawData.getString("msg");
        String publicKey = rawData.getString("pub");
        String sig = rawData.getString("sig");
        boolean verifyRet = verify(publicKey, sig, hexMsg);
        if (!verifyRet) {
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.getVERIFY_ERROR());
        }
        String did = ElaKit.getIdentityFromPublicKey(publicKey);
        String msg = new String(DatatypeConverter.parseHexBinary(hexMsg));
        DidEntity didEntity = JSON.parseObject(msg, DidEntity.class);
        String tag = didEntity.getTag();
        if (StringUtil.isBlank(tag) || !DidEntity.DID_TAG.equals(tag)) {
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.getVERIFY_DID_ERROR());
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

        return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
    }

    private static boolean verify(String hexPub, String hexSig, String hexMsg) {
        byte[] msg = DatatypeConverter.parseHexBinary(hexMsg);
        byte[] sig = DatatypeConverter.parseHexBinary(hexSig);
        byte[] pub = DatatypeConverter.parseHexBinary(hexPub);
        boolean isVerify = ElaSignTool.verify(msg, sig, pub);
        return isVerify;
    }

    public ReturnMsgEntity getPropertiesOfDidFromCache(String did){
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "id");
        List<CacheDidProperty> cacheDidProperties = didPropertyOnCacheRepository.findAllByDid(did, sort);
        if ((null == cacheDidProperties) || (cacheDidProperties.isEmpty())) {
            logger.debug("getPropertyHistoryFromCache There is no data in database. did = {}", did);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
        return new ReturnMsgEntity().setResult(JSON.toJSONString(cacheDidProperties)).setStatus(retCodeConfiguration.SUCC());
    }

    public ReturnMsgEntity getDIDPropertyFromCache(String did ,String propertyKey){
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "id");
        Optional<CacheDidProperty> cacheDidPropertyOp = didPropertyOnCacheRepository.findFirstByDidAndAndKey(did, propertyKey, sort);
        if (!cacheDidPropertyOp.isPresent()) {
            logger.debug("getPropertyHistoryFromCache There is no data in database. did = {},propertyKey={}", did, propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
        return new ReturnMsgEntity().setResult(JSON.toJSONString(cacheDidPropertyOp.get())).setStatus(retCodeConfiguration.SUCC());
    }

    public ReturnMsgEntity getPropertyHistoryFromCache(String did ,String propertyKey){
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "id");
        List<CacheDidProperty> cacheDidProperties = didPropertyOnCacheRepository.findAllByDidAndAndKey(did, propertyKey, sort);
        if ((null == cacheDidProperties) || (cacheDidProperties.isEmpty())) {
            logger.debug("getPropertyHistoryFromCache There is no data in database. did = {},propertyKey={}", did, propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
        return new ReturnMsgEntity().setResult(JSON.toJSONString(cacheDidProperties)).setStatus(retCodeConfiguration.SUCC());
    }

    public void checkAndRemoveCache(){
        List<String> txidList = didPropertyOnCacheRepository.findGroupByTxid();
        if (txidList.isEmpty()) {
            return;
        }
        ElaTransferService elaService = ElaTransferService.getInstance(ElaChainType.DID_CHAIN, nodeConfiguration.getDidPrefix(), false);
        for (String txid : txidList) {
            RetResult<String> ret = elaService.getTransactionReceipt(txid);
            if (ret.getCode() == RetCode.SUCC) {
                didPropertyOnCacheRepository.deleteByTxid(txid);
            }
        }
    }
}
