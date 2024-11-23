package com.veviosys.vdigit.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteFolderOprationRequest {
    private long userId;
    private UUID folderId;
}