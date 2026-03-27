package com.saferoute.common.service;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Student service with CRUD operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse findById(UUID id) {
        StudentEntity entity = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return toResponse(entity);
    }

    public StudentResponse create(StudentRequest request) {
        Point homeLocation = createPoint(request.homeLongitude(), request.homeLatitude());
        
        Point schoolLocation = null;
        if (request.schoolLatitude() != null && request.schoolLongitude() != null) {
            schoolLocation = createPoint(request.schoolLongitude(), request.schoolLatitude());
        }

        StudentEntity entity = StudentEntity.builder()
                .name(request.name())
                .address(request.address())
                .location(homeLocation)
                .schoolName(request.schoolName())
                .schoolLocation(schoolLocation)
                .build();

        StudentEntity saved = studentRepository.save(entity);
        log.info("Student created: {}", saved.getId());
        return toResponse(saved);
    }

    public StudentResponse update(UUID id, StudentRequest request) {
        StudentEntity entity = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.address() != null) {
            entity.setAddress(request.address());
        }
        if (request.homeLatitude() != null && request.homeLongitude() != null) {
            entity.setLocation(createPoint(request.homeLongitude(), request.homeLatitude()));
        }
        if (request.schoolName() != null) {
            entity.setSchoolName(request.schoolName());
        }
        if (request.schoolLatitude() != null && request.schoolLongitude() != null) {
            entity.setSchoolLocation(createPoint(request.schoolLongitude(), request.schoolLatitude()));
        }

        StudentEntity saved = studentRepository.save(entity);
        log.info("Student updated: {}", id);
        return toResponse(saved);
    }

    public void delete(UUID id) {
        StudentEntity entity = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        studentRepository.delete(entity);
        log.info("Student deleted: {}", id);
    }

    public boolean existsById(UUID id) {
        return studentRepository.existsById(id);
    }

    private Point createPoint(Double longitude, Double latitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    private Double getLatitude(Point point) {
        return point != null ? point.getY() : null;
    }

    private Double getLongitude(Point point) {
        return point != null ? point.getX() : null;
    }

    private StudentResponse toResponse(StudentEntity entity) {
        return StudentResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .homeLatitude(getLatitude(entity.getLocation()))
                .homeLongitude(getLongitude(entity.getLocation()))
                .schoolName(entity.getSchoolName())
                .schoolLatitude(getLatitude(entity.getSchoolLocation()))
                .schoolLongitude(getLongitude(entity.getSchoolLocation()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Custom exception
    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }
}
