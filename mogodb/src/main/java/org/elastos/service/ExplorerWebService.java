package org.elastos.service;

import com.alibaba.fastjson.JSON;
import org.elastos.constant.RetCode;
import org.elastos.entity.ChainBlockHeader;
import org.elastos.entity.ChainDidProperty;
import org.elastos.repositories.ChainBlockHeaderRepository;
import org.elastos.repositories.ChainBlockTransactionRepository;
import org.elastos.repositories.DidPropertyOnChainRepository;
import org.elastos.util.ListPage;
import org.elastos.util.RetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExplorerWebService {
    private static Logger logger = LoggerFactory.getLogger(ExplorerWebService.class);

    @Autowired
    ChainBlockHeaderRepository chainBlockHeaderRepository;

    @Autowired
    ChainBlockTransactionRepository chainBlockTransactionRepository;

    @Autowired
    DidPropertyOnChainRepository didPropertyOnChainRepository;

    @Autowired
    ElaDidMongoDbService elaDidMongoDbService;


    private String outputOne(String name, Object ob) {
        Map<String, Object> data = new HashMap<>();
        data.put(name, ob);
        List<Object> list = new ArrayList<>();
        list.add(data);
        return JSON.toJSONString(list);
    }

    private String outputList(Collection list) {
        return JSON.toJSONString(list);
    }

    public String getCurrentHeight() {
        Optional<ChainBlockHeader> headerOp = chainBlockHeaderRepository.findTopByOrderByIdDesc();
        return outputOne("height", headerOp.get().getHeight());
    }

    public String getBlockInfoByHeight(Integer height) {
        List<ChainBlockHeader> header = chainBlockHeaderRepository.findByHeight(height);
        return outputList(header);
    }

    public String getAllByHeight(Integer height) {
        List<Map<String, Object>> list = chainBlockTransactionRepository.findAllByHeight(height);
        return outputList(list);
    }

    public String getDidInfoByHeight(Integer height) {
        List<Map<String, Object>> list = chainBlockTransactionRepository.findDidInfoByHeight(height);
        return outputList(list);
    }

    public String getValueOfTxid(String txid) {
        List<Map<String, Object>> list = chainBlockTransactionRepository.findValueOfTxid(txid);
        return outputList(list);
    }

    public String getByTxid(String txid) {
        List<Map<String, Object>> list = chainBlockTransactionRepository.findByTxid(txid);
        return outputList(list);
    }

    public String getMemoInfoByTxid(String txid) {
        List<Map<String, Object>> list = chainBlockTransactionRepository.findMemoInfoByTxid(txid);
        return outputList(list);
    }

    public String findLastProperty() {
        List<Map<String, Object>> list = didPropertyOnChainRepository.findLastProperty();
        return outputList(list);
    }

    public String findCurrentPropertyHeight() {
        Integer height = elaDidMongoDbService.getCurrentBlockHeight();
        return outputOne("height", height);
    }

    public String findBlocksCount() {
        Integer count = elaDidMongoDbService.getBlockHeightCount();
        return outputOne("count", count);
    }

    public String findBlocks(Integer start, Integer size) {
        List<Map<String, Object>> list = didPropertyOnChainRepository.findBlocks(start, size);
        return outputList(list);
    }

    public String getTransactionIds(Integer height) {
        List<Map<String, Object>> list = didPropertyOnChainRepository.findTransactionIds(height);
        return outputOne("count",list.size());
    }

    public String findTransactions(Integer start, Integer size) {
        List<Map<String, Object>> list = didPropertyOnChainRepository.findTransactions(start, size);
        return outputList(list);
    }

    public String findTransactionsCount() {
        Integer count = elaDidMongoDbService.getDidTxidCount();
        return outputOne("count", count);
    }

    public String findTransactionsInfo(String txid) {
        List<Map<String, Object>> list = didPropertyOnChainRepository.findTransactionInfo(txid);
        return outputList(list);
    }

    public String findTransactionsDid(String did) {
        List<Map<String, Object>> list = didPropertyOnChainRepository.findTransactionDid(did);
        return outputList(list);
    }

    public String findPropertiesDid(String did) {
        RetResult<List<ChainDidProperty>> listRetResult = elaDidMongoDbService.getPropertiesOfDid(did);
        if (listRetResult.getCode() == RetCode.SUCC) {
            return outputList(listRetResult.getData());
        } else {
            List<Map<String, Object>> list = didPropertyOnChainRepository.findPropertiesDid(did);
            return outputList(list);
        }
    }

    public String findPropertiesHistory(String did, String key, Integer start, Integer pageSize) {
        RetResult<List<Long>> listRetResult =  elaDidMongoDbService.getPropertyHistoryIds(did, key);
        if (listRetResult.getCode() != RetCode.SUCC) {
            List<Map<String, Object>> list = didPropertyOnChainRepository.findPropertiesHistory(did, key, start, pageSize);
            return outputList(list);
        } else {
            List<Long> ids = listRetResult.getData();
            List<Long>subIds = ListPage.getListPage(ids, start, pageSize);
            List<ChainDidProperty> list = didPropertyOnChainRepository.findAllById(subIds);
            return outputList(list);
        }

    }

    public String findPropertiesHistoryCount(String did, String key) {
        RetResult<List<Long>> listRetResult =  elaDidMongoDbService.getPropertyHistoryIds(did, key);
        if (listRetResult.getCode() != RetCode.SUCC) {
            List<Map<String, Object>> list = didPropertyOnChainRepository.findPropertiesHistoryCount(did, key);
            return outputList(list);
        } else {
            List<Long> ids = listRetResult.getData();
            return outputOne("count", ids.size());
        }
    }


}
