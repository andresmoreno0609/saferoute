package com.saferoute.common.dto.student;

public record StudentRequest(
    String name,
    String address,
    String schoolName
) {}
