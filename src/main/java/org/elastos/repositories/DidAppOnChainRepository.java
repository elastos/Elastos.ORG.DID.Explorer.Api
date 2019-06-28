package org.elastos.repositories;

import org.elastos.entity.ChainDidApp;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DidAppOnChainRepository extends JpaRepository<ChainDidApp, Long>, JpaSpecificationExecutor<ChainDidApp> {
    List<ChainDidApp> findByInfoValue(String infoValue, Sort sort);
}
