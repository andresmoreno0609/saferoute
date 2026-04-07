package com.saferoute.common.dto.vehicledocument;

import jakarta.validation.constraints.NotBlank;

public record RejectDocumentRequest(
    @NotBlank(message = "El ID del usuario que verifica es obligatorio")
    String verifiedBy,
    
    @NotBlank(message = "El motivo de rechazo es obligatorio")
    String reason
) {}