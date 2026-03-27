package com.saferoute.route.usecase;

import com.saferoute.common.dto.route.RouteRequest;

import java.util.UUID;

public record UpdateRouteRequest(UUID id, RouteRequest request) {}
