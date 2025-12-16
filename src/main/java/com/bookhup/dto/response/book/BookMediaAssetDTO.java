package com.bookhup.dto.response.book;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookMediaAssetDTO {

    private Long assetId;
    private String fileUrl;
    private String type;
}

