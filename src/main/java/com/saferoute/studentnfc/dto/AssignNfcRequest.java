package com.saferoute.studentnfc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssignNfcRequest(
    @NotBlank(message = "El UID del NFC es obligatorio")
    @Size(max = 100, message = "El UID no puede exceder 100 caracteres")
    String nfcUid,

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    String notes
) {}