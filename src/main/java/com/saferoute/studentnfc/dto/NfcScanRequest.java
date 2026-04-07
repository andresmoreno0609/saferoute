package com.saferoute.studentnfc.dto;

import jakarta.validation.constraints.NotBlank;

public record NfcScanRequest(
    @NotBlank(message = "El UID del NFC es obligatorio")
    String nfcUid
) {}