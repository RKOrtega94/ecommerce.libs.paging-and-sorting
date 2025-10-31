package ec.com.ecommerce.specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for building JPA Specifications in a fluent and type-safe manner.
 * <p>
 * Example usage:
 * <pre>
 * Specification<User> spec = SpecificationBuilder.builder()
 *     .equals("username", "john")
 *     .greaterThan("age", 18)
 *     .like("email", "gmail.com")
 *     .build();
 * </pre>
 * <p>
 * Supports nested fields (e.g., "address.city"), joins, and various comparison operations.
 */
public class SpecificationBuilder<T> {
    /**
     * Private constructor to prevent instantiation from outside, but allow builder() to instantiate.
     */
    private SpecificationBuilder() {
    }

    private final List<Specification<T>> specifications = new ArrayList<>();

    /**
     * Create a new SpecificationBuilder instance.
     * <p>
     * Example:
     * <pre>
     * SpecificationBuilder<User> builder = SpecificationBuilder.builder();
     * </pre>
     */
    public static <T> SpecificationBuilder<T> builder() {
        return new SpecificationBuilder<>();
    }

    /**
     * Equals operation for simple fields or Many-to-one relationships.
     * <p>
     * Example:
     * <pre>
     * builder.equals("status", Status.ACTIVE);
     * </pre>
     *
     * @param field the name of field (supports dot notation for nested fields)
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> equals(String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Path<?> path = getPath(root, field);
                return cb.equal(path, value);
            });
        }
        return this;
    }

    /**
     * Not equals operation for simple fields or Many-to-one relationships.
     * <p>
     * Example:
     * <pre>
     * builder.notEquals("status", Status.INACTIVE);
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> notEquals(String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Path<?> path = getPath(root, field);
                return cb.notEqual(path, value);
            });
        }
        return this;
    }

    /**
     * Like operation for string fields (case-insensitive).
     * <p>
     * Example:
     * <pre>
     * builder.like("name", "john");
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> like(String field, String value) {
        if (value != null && !value.isEmpty()) {
            specifications.add((root, query, cb) -> {
                Path<String> path = getPath(root, field);
                return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
            });
        }
        return this;
    }

    /**
     * Starts with operation for string fields (case-insensitive).
     * <p>
     * Example:
     * <pre>
     * builder.startsWith("name", "A");
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> startsWith(String field, String value) {
        if (value != null && !value.isEmpty()) {
            specifications.add((root, query, cb) -> {
                Path<String> path = getPath(root, field);
                return cb.like(cb.lower(path), value.toLowerCase() + "%");
            });
        }
        return this;
    }

    /**
     * Ends with operation for string fields (case-insensitive).
     * <p>
     * Example:
     * <pre>
     * builder.endsWith("name", "son");
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> endsWith(String field, String value) {
        if (value != null && !value.isEmpty()) {
            specifications.add((root, query, cb) -> {
                Path<String> path = getPath(root, field);
                return cb.like(cb.lower(path), "%" + value.toLowerCase());
            });
        }
        return this;
    }

    /**
     * Greater than operation for comparable fields.
     * <p>
     * Example:
     * <pre>
     * builder.greaterThan("age", 18);
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @param <Y>   the type of the field
     * @return the current SpecificationBuilder instance
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> greaterThan(String field, Y value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Path<Y> path = getPath(root, field);
                return cb.greaterThan(path, value);
            });
        }
        return this;
    }

    /**
     * Greater than or equal to operation for comparable fields.
     * <p>
     * Example:
     * <pre>
     * builder.greaterThanOrEqualTo("age", 18);
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @param <Y>   the type of the field
     * @return the current SpecificationBuilder instance
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> greaterThanOrEqualTo(String field, Y value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Path<Y> path = getPath(root, field);
                return cb.greaterThanOrEqualTo(path, value);
            });
        }
        return this;
    }

    /**
     * Less than operation for comparable fields.
     * <p>
     * Example:
     * <pre>
     * builder.lessThan("age", 65);
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @param <Y>   the type of the field
     * @return the current SpecificationBuilder instance
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> lessThan(String field, Y value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Path<Y> path = getPath(root, field);
                return cb.lessThan(path, value);
            });
        }
        return this;
    }

    /**
     * Less than or equal to operation for comparable fields.
     * <p>
     * Example:
     * <pre>
     * builder.lessThanOrEqualTo("age", 65);
     * </pre>
     *
     * @param field the name of field
     * @param value the value to compare
     * @param <Y>   the type of the field
     * @return the current SpecificationBuilder instance
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> lessThanOrEqualTo(String field, Y value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Path<Y> path = getPath(root, field);
                return cb.lessThanOrEqualTo(path, value);
            });
        }
        return this;
    }

    /**
     * Between operation for comparable fields.
     * <p>
     * Example:
     * <pre>
     * builder.between("age", 18, 65);
     * </pre>
     *
     * @param field the name of field
     * @param start the start value
     * @param end   the end value
     * @param <Y>   the type of the field
     * @return the current SpecificationBuilder instance
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> between(String field, Y start, Y end) {
        if (start != null && end != null) {
            specifications.add((root, query, cb) -> {
                Path<Y> path = getPath(root, field);
                return cb.between(path, start, end);
            });
        }
        return this;
    }

    /**
     * Is null operation for any field.
     * <p>
     * Example:
     * <pre>
     * builder.isNull("deletedAt");
     * </pre>
     *
     * @param field the name of field
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> isNull(String field) {
        specifications.add((root, query, cb) -> {
            Path<?> path = getPath(root, field);
            return cb.isNull(path);
        });
        return this;
    }

    /**
     * Is not null operation for any field.
     * <p>
     * Example:
     * <pre>
     * builder.isNotNull("updatedAt");
     * </pre>
     *
     * @param field the name of field
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> isNotNull(String field) {
        specifications.add((root, query, cb) -> {
            Path<?> path = getPath(root, field);
            return cb.isNotNull(path);
        });
        return this;
    }

    /**
     * In operation for simple fields or Many-to-one relationships.
     * <p>
     * Example:
     * <pre>
     * builder.in("role", List.of(Role.ADMIN, Role.USER));
     * </pre>
     *
     * @param field  the name of field
     * @param values the collection of values to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> in(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            specifications.add((root, query, cb) -> {
                Path<?> path = getPath(root, field);
                return path.in(values);
            });
        }
        return this;
    }

    /**
     * Not in operation for simple fields or Many-to-one relationships.
     * <p>
     * Example:
     * <pre>
     * builder.notIn("role", List.of(Role.GUEST));
     * </pre>
     *
     * @param field  the name of field
     * @param values the collection of values to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> notIn(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            specifications.add((root, query, cb) -> {
                Path<?> path = getPath(root, field);
                return cb.not(path.in(values));
            });
        }
        return this;
    }

    /**
     * Equals operation for Many-to-many relationships (INNER JOIN).
     * <p>
     * Example:
     * <pre>
     * builder.joinEquals("roles.name", "ADMIN");
     * </pre>
     *
     * @param field the name of the field, using dot notation for nested fields (e.g., "roles.name")
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> joinEquals(String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                String[] parts = field.split("\\.");
                Path<?> path = root.join(parts[0], JoinType.INNER);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                Objects.requireNonNull(query).distinct(true);
                return cb.equal(path, value);
            });
        }
        return this;
    }

    /**
     * Like operation for Many-to-many relationships (INNER JOIN).
     * <p>
     * Example:
     * <pre>
     * builder.joinLike("roles.name", "admin");
     * </pre>
     *
     * @param field the name of the field, using dot notation for nested fields (e.g., "roles.name")
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> joinLike(String field, String value) {
        if (value != null && !value.isEmpty()) {
            specifications.add((root, query, cb) -> {
                String[] parts = field.split("\\.");
                Path<String> path = root.join(parts[0], JoinType.INNER);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                Objects.requireNonNull(query).distinct(true);
                return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
            });
        }
        return this;
    }

    /**
     * In operation for Many-to-many relationships (INNER JOIN).
     * <p>
     * Example:
     * <pre>
     * builder.joinIn("roles.name", List.of("ADMIN", "USER"));
     * </pre>
     *
     * @param field  the name of the field, using dot notation for nested fields (e.g., "roles.name")
     * @param values the collection of values to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> joinIn(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            specifications.add((root, query, cb) -> {
                String[] parts = field.split("\\.");
                Path<?> path = root.join(parts[0], JoinType.INNER);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                Objects.requireNonNull(query).distinct(true);
                return path.in(values);
            });
        }
        return this;
    }

    /**
     * Equals operation for Many-to-many relationships using LEFT JOIN.
     * <p>
     * Example:
     * <pre>
     * builder.leftJoinEquals("roles.name", "ADMIN");
     * </pre>
     *
     * @param field the name of the field, using dot notation for nested fields (e.g., "roles.name")
     * @param value the value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> leftJoinEquals(String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                String[] parts = field.split("\\.");
                Path<?> path = root.join(parts[0], JoinType.LEFT);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                Objects.requireNonNull(query).distinct(true);
                return cb.equal(path, value);
            });
        }
        return this;
    }

    /**
     * Date equals operation for LocalDate fields.
     * <p>
     * Example:
     * <pre>
     * builder.dateEquals("createdDate", LocalDate.now());
     * </pre>
     *
     * @param field the name of the field
     * @param date  the LocalDate value to compare
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> dateEquals(String field, LocalDate date) {
        if (date != null) {
            specifications.add((root, query, cb) -> {
                Path<LocalDate> path = getPath(root, field);
                return cb.equal(path, date);
            });
        }
        return this;
    }

    /**
     * Date between operation for LocalDate fields.
     * <p>
     * Example:
     * <pre>
     * builder.dateBetween("createdDate", startDate, endDate);
     * </pre>
     *
     * @param field the name of the field
     * @param start the start LocalDate value
     * @param end   the end LocalDate value
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> dateBetween(String field, LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            specifications.add((root, query, cb) -> {
                Path<LocalDate> path = getPath(root, field);
                return cb.between(path, start, end);
            });
        }
        return this;
    }

    /**
     * DateTime between operation for LocalDateTime fields.
     * <p>
     * Example:
     * <pre>
     * builder.dateTimeBetween("createdAt", startDateTime, endDateTime);
     * </pre>
     *
     * @param field the name of the field
     * @param start the LocalDateTime value to compare
     * @param end   the end LocalDateTime value
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> dateTimeBetween(String field, LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            specifications.add((root, query, cb) -> {
                Path<LocalDateTime> path = getPath(root, field);
                return cb.between(path, start, end);
            });
        }
        return this;
    }

    /**
     * Combine the last specification with the given specification using OR.
     * <p>
     * Example:
     * <pre>
     * builder.equals("status", Status.ACTIVE).or((root, query, cb) -> cb.equal(root.get("status"), Status.PENDING));
     * </pre>
     *
     * @param specification an additional specification to combine with OR
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> or(Specification<T> specification) {
        if (!specifications.isEmpty()) {
            Specification<T> lastSpec = specifications.removeLast();
            specifications.add(lastSpec.or(specification));
        } else if (specification != null) {
            specifications.add(specification);
        }
        return this;
    }

    /**
     * Add a custom specification.
     * <p>
     * Example:
     * <pre>
     * builder.custom((root, query, cb) -> cb.isTrue(root.get("active")));
     * </pre>
     *
     * @param specification custom specification to add
     * @return the current SpecificationBuilder instance
     */
    public SpecificationBuilder<T> custom(Specification<T> specification) {
        if (specification != null) {
            specifications.add(specification);
        }
        return this;
    }

    /**
     * Build the final Specification by combining all added specifications with AND.
     * <p>
     * Example:
     * <pre>
     * Specification<User> spec = builder.build();
     * </pre>
     *
     * @return the combined Specification
     */
    public Specification<T> build() {
        if (specifications.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        Specification<T> result = specifications.getFirst();
        for (int i = 1; i < specifications.size(); i++) {
            result = result.and(specifications.get(i));
        }
        return result;
    }

    /**
     * Get path for nested fields (dot notation supported).
     * <p>
     * Example:
     * <pre>
     * Path<?> path = getPath(root, "address.city");
     * </pre>
     *
     * @param root  the root
     * @param field the field (dot notation for nested fields)
     * @param <Y>   the type of the field
     * @return the path
     */
    @SuppressWarnings("unchecked")
    private <Y> Path<Y> getPath(Root<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return (Path<Y>) path;
    }
}
