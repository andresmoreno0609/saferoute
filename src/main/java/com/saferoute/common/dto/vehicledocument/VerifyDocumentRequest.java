package com.saferoute.common.dto.vehicledocument;

import jakarta.validation.constraints.NotBlank;

public record VerifyDocumentRequest(
    @NotBlank(message = "El ID del usuario que verifica es obligatorio")
    String verifiedBy
) {}