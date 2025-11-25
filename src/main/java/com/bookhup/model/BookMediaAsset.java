package com.bookhup.model;

import jakarta.persistence.*;
import lombok.*;

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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column(name = "type", length = 30)
    private String type;
}
