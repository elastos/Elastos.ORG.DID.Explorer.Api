package org.elastos.repositories;

import org.elastos.entity.ChainDidProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

@Repository
public interface DidPropertyOnChainRepository extends JpaRepository<ChainDidProperty, Long>, JpaSpecificationExecutor<ChainDidProperty> {
    List<ChainDidProperty> findByDid(String did, Sort sort);

    List<ChainDidProperty> findFirst100ByIdGreaterThan(Long start, Sort sort);

    List<ChainDidProperty> findFirst10000ByIdGreaterThan(Long start, Sort sort);

    List<ChainDidProperty> findAllByDidStatus(Integer status);

    List<ChainDidProperty> findAllByTxid(String txid);

    @Query(nativeQuery = true, value = "SELECT * FROM `chain_did_property` ORDER BY `id` desc LIMIT 1")
    List<Map<String, Object>> findLastProperty();

    @Query(nativeQuery = true, value = "SELECT `height` FROM `chain_did_property` GROUP BY `height` ORDER BY `height` DESC LIMIT ?1, ?2")
    List<Map<String, Object>> findBlocks(Integer start, Integer size);

    @Query(nativeQuery = true, value = "SELECT `height`,`txid` FROM chain_did_property  WHERE `height` = ?1 GROUP BY `txid`")
    List<Map<String, Object>> findTransactionIds(Integer height);

    @Query(nativeQuery = true, value = "SELECT distinct did, txid, height FROM `chain_did_property`  ORDER BY id DESC LIMIT ?1, ?2")
    List<Map<String, Object>> findTransactions(Integer start, Integer size);

    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM `chain_did_property` WHERE txid = ?1 ORDER BY `block_time` DESC) a GROUP BY `property_key`")
    List<Map<String, Object>> findTransactionInfo(String txid);

    @Query(nativeQuery = true, value = "SELECT * FROM `chain_did_property`  WHERE `did` = ?1 ORDER BY `id` DESC LIMIT 5")
    List<Map<String, Object>> findTransactionDid(String did);

    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM `chain_did_property` WHERE did = ?1 ORDER BY `block_time` DESC) a GROUP BY `property_key`")
    List<Map<String, Object>> findPropertiesDid(String did);

    @Query(nativeQuery = true, value = "SELECT * FROM `chain_did_property`  WHERE `did` = ?1 AND `property_key` = ?2 ORDER BY `id` DESC LIMIT ?2, ?3")
    List<Map<String, Object>> findPropertiesHistory(String did, String key, Integer start, Integer pageSize);

    @Query(nativeQuery = true, value = "SELECT count(*) AS count FROM `chain_did_property`  WHERE `did` = ?1 AND `property_key` = ?2")
    List<Map<String, Object>> findPropertiesHistoryCount(String did, String key);
}
