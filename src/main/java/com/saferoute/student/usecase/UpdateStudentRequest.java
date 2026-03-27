package com.saferoute.student.usecase;

import com.saferoute.common.dto.student.StudentRequest;

import java.util.UUID;

/**
 * Request record for updating a student.
 */
public record UpdateStudentRequest(UUID id, StudentRequest request) {}
