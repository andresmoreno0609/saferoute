package com.saferoute.stop.usecase;

import com.saferoute.common.dto.stop.StopRequest;

import java.util.UUID;

public record UpdateStopRequest(UUID id, StopRequest request) {}
