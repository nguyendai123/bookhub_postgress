package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book_media_asset")
public class BookMediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column(name = "type", length = 30)
    private String type;
}
