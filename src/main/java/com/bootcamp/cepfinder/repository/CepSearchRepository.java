package com.bootcamp.cepfinder.repository;

import com.bootcamp.cepfinder.model.CepSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CepSearchRepository extends JpaRepository<CepSearch, Long> {

    List<CepSearch> findAllByOrderByBuscadoEmDesc(Pageable pageable);

    @Modifying
    @Query("DELETE FROM CepSearch c WHERE c.id = :id")
    int deleteByIdReturningCount(Long id);
}
