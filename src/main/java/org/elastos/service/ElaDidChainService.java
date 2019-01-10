package org.elastos.service;

import com.alibaba.fastjson.JSON;
import org.elastos.POJO.DidProperty;
import org.elastos.POJO.DidStatus;
import org.elastos.POJO.InputDidStatus;
import org.elastos.conf.NodeConfiguration;
import org.elastos.conf.RetCodeConfiguration;
import org.elastos.entity.ChainDidProperty;
import org.elastos.entity.ChainType;
import org.elastos.entity.RawTxEntity;
import org.elastos.entity.ReturnMsgEntity;
import org.elastos.repositories.DidPropertyOnChainRepository;
import org.elastos.util.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.elastos.POJO.InputDidStatus.normal;


@Service
public class ElaDidChainService {
    public enum ReqMethod {
        GET,
        POST
    }

    @Autowired
    private NodeConfiguration nodeConfiguration;

    @Autowired
    private RetCodeConfiguration retCodeConfiguration;

    @Autowired
    private DidPropertyOnChainRepository didPropertyOnChainRepository;

    private static Logger logger = LoggerFactory.getLogger(ElaDidChainService.class);

    public String getUtxosByAddr(String address, ChainType type) {
        String ret = elaReqChainData(ReqMethod.GET, nodeConfiguration.getUtxoByAddr(type), address);
        return ret;
    }

    public String sendTransaction(RawTxEntity rawTx, ChainType type) {
        String jsonEntity = JSON.toJSONString(rawTx);
        String ret = elaReqChainData(ReqMethod.POST, nodeConfiguration.sendRawTransaction(type), jsonEntity);
        return ret;
    }

    public String getTransaction(String txId, ChainType type) {
        String ret = elaReqChainData(ReqMethod.GET, nodeConfiguration.getTransaction(type), txId);
        return ret;
    }

    private String elaReqChainData(ReqMethod method, String url, String data) {
        if (ReqMethod.GET == method) {
            String str = url;
            if (null != data) {
                str += data;
            }
            return HttpKit.get(str);
        } else {
            return HttpKit.post(url, data);
        }
    }

    public ReturnMsgEntity getDIDDetailedProperties(String did, InputDidStatus status, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "txid");

        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDDetailedProperties There is no data in database. did = {},status={}", did, status);
            System.out.println("getDIDDetailedProperties There is no data in database. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        Set<ChainDidProperty> propertySet = new HashSet<>();
        if (!filterValidDidDetailedProperties(onChainProperties, null, propertySet)) {
            logger.debug("getDIDDetailedProperties The did is deprecated. did = {},status={}", did, status);
            System.out.println("getDIDDetailedProperties The did is deprecated. did=" + did + ",status=" + status);
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
            logger.debug("getDIDDetailedProperties There is no property data . did = {},status={}", did, status);
            System.out.println("getDIDDetailedProperties There is no property data. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDProperties(String did, InputDidStatus status, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "txid");

        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDProperties There is no data in database. did = {},status={}", did, status);
            System.out.println("getDIDProperties There is no data in database. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        Set<DidProperty> propertySet = new HashSet<>();
        if (!filterValidDidProperties(onChainProperties, null, propertySet)) {
            logger.debug("getDIDProperties The did is deprecated. did = {},status={}", did, status);
            System.out.println("getDIDProperties The did is deprecated. did=" + did + ",status=" + status);
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
            logger.debug("getDIDProperties There is no property data . did = {},status={}", did, status);
            System.out.println("getDIDProperties There is no property data. did=" + did + ",status=" + status);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDPropertyValue(String did, String propertyKey) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "txid");
        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDPropertyValue There is no data in database. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyValue There is no data in database. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        //Use database descendant list and Set filter the latest data in property.
        Set<DidProperty> propertySet = new HashSet<>();
        if (!filterValidDidProperties(onChainProperties, propertyKey, propertySet)) {
            logger.debug("getDIDPropertyValue The did is deprecated. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyValue The did is deprecated. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }

        propertySet.removeIf(property -> property.getStatus() == InputDidStatus.deprecated);

        if (!propertySet.isEmpty()) {
            String ret = propertiesToJson(new ArrayList<>(propertySet), null, null);
            return new ReturnMsgEntity().setResult(ret).setStatus(retCodeConfiguration.SUCC());
        } else {
            logger.debug("getDIDPropertyValue There is no property data. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyValue There is no property data. did=" + did + ",status=" + propertyKey);
            return new ReturnMsgEntity().setResult("").setStatus(retCodeConfiguration.SUCC());
        }
    }

    public ReturnMsgEntity getDIDPropertyHistory(String did, String propertyKey, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blockTime", "txid");
        List<ChainDidProperty> onChainProperties = didPropertyOnChainRepository.findByDid(did, sort);
        if ((null == onChainProperties) || (onChainProperties.isEmpty())) {
            logger.debug("getDIDPropertyHistory There is no data in database. did = {},propertyKey={}", did, propertyKey);
            System.out.println("getDIDPropertyHistory getDIDPropertyValue There is no data in database. did=" + did + ",status=" + propertyKey);
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

    private String propertiesToJson(List<DidProperty> properties, Integer page, Integer size) {
        //clean status, do not output it for user.
        properties.forEach(pro -> pro.setStatus(null));
        if (null == page) {
            return JSON.toJSONString(properties);
        } else {
            Pageable pageable = PageRequest.of(page, (null == size) ? 20 : size);
            int start = (int)pageable.getOffset();
            int end = (start + pageable.getPageSize()) > properties.size() ? properties.size() : (start + pageable.getPageSize());
            List<DidProperty> subList = properties.subList(start, end);
            return JSON.toJSONString(subList);
        }
    }

    private String detailedPropertiesToJson(List<ChainDidProperty> properties, Integer page, Integer size) {
        properties.forEach(pro -> pro.setPropertyStatus(pro.getPropertyStatus().equals("1")? DidStatus.Normal.toString(): DidStatus.Deprecated.toString()));
        properties.forEach(pro -> pro.setDidStatus(pro.getDidStatus().equals("1")? DidStatus.Normal.toString(): DidStatus.Deprecated.toString()));
        if (null == page) {
            return JSON.toJSONString(properties);
        } else {
            Pageable pageable = PageRequest.of(page, (null == size) ? 20 : size);
            int start = (int)pageable.getOffset();
            int end = (start + pageable.getPageSize()) > properties.size() ? properties.size() : (start + pageable.getPageSize());
            List<ChainDidProperty> subList = properties.subList(start, end);
            return JSON.toJSONString(subList);
        }
    }

    private boolean filterValidDidDetailedProperties(List<ChainDidProperty> propertyDescList, String propertyKey, Collection<ChainDidProperty> properties) {
        //Every time has to
        for (ChainDidProperty p : propertyDescList) {
            if ("1".equals(p.getDidStatus())) {
                //If propertyKey is null, means get all properties.
                if (null == propertyKey) {
                    properties.add(p);
                } else {
                    //If has propertyKey, we filter the just property.
                    if (propertyKey.equals(p.getPropertyKey())) {
                        properties.add(p);
                    }
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
        didProperty.setKey(p.getPropertyKey());
        didProperty.setValue(p.getPropertyValue());
        if ("1".equals(p.getPropertyStatus())) {
            didProperty.setStatus(normal);
        } else {
            didProperty.setStatus(InputDidStatus.deprecated);
        }
        properties.add(didProperty);
    }
}
