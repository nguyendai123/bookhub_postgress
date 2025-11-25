package com.bookhup.repository;

import com.bookhup.model.BookMediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMediaAssetRepository extends JpaRepository<BookMediaAsset, Long> {
}

