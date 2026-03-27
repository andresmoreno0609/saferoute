package com.saferoute.common.service;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService extends BaseCrudService<StudentEntity, StudentRequest, StudentResponse, UUID> {

    private final StudentRepository studentRepository;

    @Override
    protected StudentRepository getRepository() {
        return studentRepository;
    }

    @Override
    protected StudentEntity toEntity(StudentRequest request) {
        return StudentEntity.builder()
                .name(request.name())
                .address(request.address())
                .schoolName(request.schoolName())
                .build();
    }

    @Override
    protected StudentResponse toResponse(StudentEntity entity) {
        return new StudentResponse(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getSchoolName(),
                entity.getCreatedAt()
        );
    }

    @Override
    protected void updateEntity(StudentRequest request, StudentEntity entity) {
        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.address() != null) {
            entity.setAddress(request.address());
        }
        if (request.schoolName() != null) {
            entity.setSchoolName(request.schoolName());
        }
    }
}
