package org.elastos.repositories;

import org.elastos.entity.ChainDidProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DidPropertyOnChainRepository extends JpaRepository<ChainDidProperty, Long>, JpaSpecificationExecutor<ChainDidProperty> {
    List<ChainDidProperty> findByDid(String did, Sort sort);

    List<ChainDidProperty> findByDidAndHeightIsGreaterThanEqual(String did, Integer Height, Sort sort);

    List<ChainDidProperty> findByPropertyKey(String propertyKey, Sort sort);
}
