package com.gestioneFoto.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    // Puoi aggiungere altri campi come ruoli, expiry, ecc.
}
