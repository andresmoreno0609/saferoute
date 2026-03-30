package com.saferoute.studentevent.usecase;

import java.util.UUID;

public record StudentLastEventRequest(UUID studentId, UUID routeId) {}
