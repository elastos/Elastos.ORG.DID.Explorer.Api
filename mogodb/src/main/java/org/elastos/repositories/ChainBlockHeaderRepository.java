package org.elastos.repositories;

import org.elastos.entity.ChainBlockHeader;
import org.elastos.entity.ChainDidProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChainBlockHeaderRepository extends JpaRepository<ChainBlockHeader, Long>, JpaSpecificationExecutor<ChainBlockHeader> {
    Optional<ChainBlockHeader> findTopByOrderByIdDesc();

    List<ChainBlockHeader> findByHeight(Integer height);


//    List<ChainDidProperty> findByDid(String did, Sort sort);
//
//    List<ChainDidProperty> findByDidAndHeightIsGreaterThanEqual(String did, Integer Height, Sort sort);
//
//    List<ChainDidProperty> findByPropertyKey(String propertyKey, Sort sort);
//

}
