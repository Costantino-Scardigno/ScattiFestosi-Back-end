package com.gestioneFoto.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareLinkResponse {
    private String shareCode;
    private String shareUrl;
    private String albumName;
}