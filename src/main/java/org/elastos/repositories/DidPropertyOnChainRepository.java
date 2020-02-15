package org.elastos.repositories;

import org.elastos.entity.ChainDidProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DidPropertyOnChainRepository extends JpaRepository<ChainDidProperty, Long>, JpaSpecificationExecutor<ChainDidProperty> {
    List<ChainDidProperty> findByDid(String did, Sort sort);

    List<ChainDidProperty> findByDidAndHeightIsGreaterThanEqual(String did, Integer Height, Sort sort);

    List<ChainDidProperty> findByPropertyKey(String propertyKey, Sort sort);

    List<ChainDidProperty> findAllByTxid(String txid);

    @Query("select t.txid as txid from ChainDidProperty t group by t.txid")
    List<String> findGroupByTxid();

    @Transactional
    void deleteByTxid(String txid);
}


