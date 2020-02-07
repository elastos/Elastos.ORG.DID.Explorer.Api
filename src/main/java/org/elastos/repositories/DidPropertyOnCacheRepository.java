package org.elastos.repositories;

import org.elastos.entity.CacheDidProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DidPropertyOnCacheRepository extends JpaRepository<CacheDidProperty, Long>, JpaSpecificationExecutor<CacheDidProperty> {
    List<CacheDidProperty> findAllByDid(String did, Sort sort);
    Optional<CacheDidProperty> findFirstByDidAndAndKey(String did, String key, Sort sort);
    List<CacheDidProperty> findAllByDidAndAndKey(String did, String key, Sort sort);

    @Query("select t.txid as txid from CacheDidProperty t group by t.txid")
    List<String> findGroupByTxid();

    @Transactional
    void deleteByTxid(String txid);
}
