package org.elastos.repositories;

import org.elastos.entity.ChainBlockTransactionHistory;
import org.elastos.entity.ChainDidProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Queue;

@Repository
public interface ChainBlockTransactionRepository extends JpaRepository<ChainBlockTransactionHistory, Long>, JpaSpecificationExecutor<ChainBlockTransactionHistory> {
    @Query(nativeQuery = true, value = "SELECT `txid` FROM `chain_block_transaction_history`  WHERE `height` = ?1 AND `txType` = \"TransferAsset\" GROUP BY `txid` ORDER BY `id` DESC")
    List<Map<String, Object>> findAllByHeight(Integer height);

    @Query(nativeQuery = true, value = "SELECT a.id,a.txid,a.height,b.createTime,a.did,a.did_status,b.memo,b.fee,b.type FROM `chain_did_property` AS a LEFT JOIN `chain_block_transaction_history` AS b ON (a.txid=b.txid) WHERE (a.height = ?1 AND  b.type = \"spend\") GROUP BY a.txid ORDER BY a.id DESC")
    List<Map<String, Object>> findDidInfoByHeight(Integer height);

    @Query(nativeQuery = true, value = "SELECT sum(a.value) value FROM `chain_block_transaction_history` a  WHERE a.txid = ?1 AND a.type = \"spend\"")
    List<Map<String, Object>> findValueOfTxid(String txid);

    @Query(nativeQuery = true, value = "SELECT * FROM `chain_block_transaction_history`  WHERE `txid` = ?1 AND `txType` = \"TransferAsset\" AND `type` = \"spend\" GROUP BY `txid` ORDER BY `id` DESC")
    List<Map<String, Object>> findByTxid(String txid);

    @Query(nativeQuery = true, value = "SELECT createTime,length(memo) AS `length_memo` FROM `chain_block_transaction_history` WHERE `type` = \"spend\" AND `txid` = ?1")
    List<Map<String, Object>> findMemoInfoByTxid(String txid);
}
