package com.saferoute.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Base CRUD service that provides standard create, update, delete, find, and search operations.
 * <p>
 * Subclasses must implement the abstract methods to define how entities are mapped
 * and which repository is used.
 *
 * @param <E>  Entity type
 * @param <REQ> Request DTO type (input for create/update)
 * @param <RES> Response DTO type (output)
 * @param <ID>  Entity identifier type
 */
public abstract class BaseCrudService<E, REQ, RES, ID> {

    /**
     * Returns the JPA repository for this entity.
     * Must extend both JpaRepository and JpaSpecificationExecutor.
     *
     * @return the repository instance
     */
    protected abstract JpaRepository<E, ID> getRepository();

    /**
     * Returns the repository as JpaSpecificationExecutor for search operations.
     *
     * @return the repository as specification executor
     */
    private JpaSpecificationExecutor<E> getSpecificationExecutor() {
        return (JpaSpecificationExecutor<E>) getRepository();
    }

    /**
     * Converts a request DTO to an entity.
     *
     * @param request the request DTO
     * @return the entity
     */
    protected abstract E toEntity(REQ request);

    /**
     * Converts an entity to a response DTO.
     *
     * @param entity the entity
     * @return the response DTO
     */
    protected abstract RES toResponse(E entity);

    /**
     * Updates an existing entity with data from the request DTO.
     * Called during update operations.
     *
     * @param request the request DTO
     * @param entity  the entity to update
     */
    protected abstract void updateEntity(REQ request, E entity);

    // ==================== CRUD OPERATIONS ====================

    /**
     * Creates a new entity.
     *
     * @param request the request DTO
     * @return the response DTO of the created entity
     */
    public RES create(REQ request) {
        E entity = toEntity(request);
        E saved = getRepository().save(entity);
        return toResponse(saved);
    }

    /**
     * Updates an existing entity.
     *
     * @param id      the entity ID
     * @param request the request DTO with updated data
     * @return the response DTO of the updated entity
     */
    public RES update(ID id, REQ request) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        updateEntity(request, entity);
        E saved = getRepository().save(entity);

        return toResponse(saved);
    }

    /**
     * Deletes an entity by ID.
     *
     * @param id the entity ID
     */
    public void delete(ID id) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        getRepository().delete(entity);
    }

    /**
     * Finds an entity by ID.
     *
     * @param id the entity ID
     * @return the response DTO
     */
    public RES findById(ID id) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        return toResponse(entity);
    }

    /**
     * Returns all entities.
     *
     * @return list of response DTOs
     */
    public List<RES> findAll() {
        return getRepository().findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Searches entities with pagination and optional filters.
     *
     * @param page     page number (0-based)
     * @param size     page size
     * @param sortBy   field to sort by
     * @param direction sort direction (ASC or DESC)
     * @param filters  list of filter criteria (can be null or empty)
     * @return page of response DTOs
     */
    public PageResult<RES> search(Integer page, Integer size, String sortBy, 
                                   String direction, List<FilterCriteria> filters) {
        
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
        
        Specification<E> spec = buildSpecification(filters);
        
        Page<E> entityPage = getSpecificationExecutor().findAll(spec, pageable);
        
        return PageResult.of(
                entityPage.getContent().stream().map(this::toResponse).toList(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    /**
     * Builds a Specification from filter criteria.
     * Override this method to add custom filter logic.
     *
     * @param filters list of filter criteria
     * @return specification for JPA query
     */
    protected Specification<E> buildSpecification(List<FilterCriteria> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        
        Specification<E> spec = null;
        
        for (FilterCriteria filter : filters) {
            Specification<E> currentSpec = new FilterCriteriaSpecification<>(filter);
            if (spec == null) {
                spec = currentSpec;
            } else {
                spec = spec.and(currentSpec);
            }
        }
        
        return spec;
    }

    /**
     * Checks if an entity exists by ID.
     *
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    /**
     * Counts all entities.
     *
     * @return total count
     */
    public long count() {
        return getRepository().count();
    }

    // ==================== EXCEPTION ====================

    /**
     * Exception thrown when an entity is not found.
     */
    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    // ==================== INNER CLASSES ====================

    /**
     * Represents filter criteria for searching entities.
     */
    public static class FilterCriteria {
        private String field;
        private String operator;
        private Object value;

        public FilterCriteria() {}

        public FilterCriteria(String field, String operator, Object value) {
            this.field = field;
            this.operator = operator;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    /**
     * Represents the result of a paginated search.
     */
    public static class PageResult<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public PageResult() {}

        public PageResult(List<T> content, int page, int size, long totalElements, int totalPages) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

        public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements, int totalPages) {
            return new PageResult<>(content, page, size, totalElements, totalPages);
        }

        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

    /**
     * Implementation of Specification for FilterCriteria.
     */
    private static class FilterCriteriaSpecification<T> implements Specification<T> {
        
        private final FilterCriteria criteria;

        public FilterCriteriaSpecification(FilterCriteria criteria) {
            this.criteria = criteria;
        }

        @Override
        public jakarta.persistence.criteria.Predicate toPredicate(jakarta.persistence.criteria.Root<T> root,
                                                                    jakarta.persistence.criteria.CriteriaQuery<?> query,
                                                                    jakarta.persistence.criteria.CriteriaBuilder builder) {
            
            String field = criteria.getField();
            String operator = criteria.getOperator();
            Object value = criteria.getValue();

            if (field == null || operator == null) {
                return null;
            }

            jakarta.persistence.criteria.Path<?> path = root.get(field);

            return switch (operator.toUpperCase()) {
                case "EQ" -> builder.equal(path, value);
                case "NE" -> builder.notEqual(path, value);
                case "LIKE" -> builder.like(path.as(String.class), "%" + value + "%");
                case "GT" -> builder.greaterThan(path.as(String.class), value.toString());
                case "LT" -> builder.lessThan(path.as(String.class), value.toString());
                case "GTE" -> builder.greaterThanOrEqualTo(path.as(String.class), value.toString());
                case "LTE" -> builder.lessThanOrEqualTo(path.as(String.class), value.toString());
                case "IS_NULL" -> builder.isNull(path);
                case "IS_NOT_NULL" -> builder.isNotNull(path);
                default -> builder.equal(path, value);
            };
        }
    }
}
