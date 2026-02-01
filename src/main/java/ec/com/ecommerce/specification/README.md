# SpecificationBuilder

A fluent and type-safe utility class for building JPA Specifications in Spring Data JPA applications. This builder simplifies the creation of complex database queries with a clean, chainable API.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [Available Operations](#available-operations)
  - [Equality Operations](#equality-operations)
  - [String Operations](#string-operations)
  - [Comparison Operations](#comparison-operations)
  - [Range Operations](#range-operations)
  - [Null Checks](#null-checks)
  - [Collection Operations](#collection-operations)
  - [Join Operations](#join-operations)
  - [Date Operations](#date-operations)
  - [Logical Operations](#logical-operations)
  - [Custom Specifications](#custom-specifications)
- [Advanced Examples](#advanced-examples)
- [Best Practices](#best-practices)

## Overview

`SpecificationBuilder` provides a fluent API to build JPA Specifications without writing verbose criteria API code. It supports:

- Simple field comparisons
- Nested field access (dot notation)
- Many-to-one and many-to-many relationships
- Inner and left joins
- Date and time operations
- Logical OR combinations
- Custom specifications

## Basic Usage

### Simple Query

```java
import ec.com.ecommerce.specification.SpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

// Find users with username "john"
Specification<User> spec = SpecificationBuilder.<User>builder()
    .equals("username", "john")
    .build();

List<User> users = userRepository.findAll(spec);
```

### Multiple Conditions (AND)

By default, all conditions are combined with AND:

```java
Specification<User> spec = SpecificationBuilder.<User>builder()
    .equals("status", Status.ACTIVE)
    .greaterThan("age", 18)
    .like("email", "gmail.com")
    .build();

// SQL: WHERE status = 'ACTIVE' AND age > 18 AND email LIKE '%gmail.com%'
```

### With Pagination and Sorting

```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());

Specification<User> spec = SpecificationBuilder.<User>builder()
    .equals("status", Status.ACTIVE)
    .build();

Page<User> users = userRepository.findAll(spec, pageable);
```

## Available Operations

### Equality Operations

#### `equals(String field, Object value)`
Exact match comparison.

```java
.equals("username", "john")
.equals("status", Status.ACTIVE)
.equals("user.company.id", 123L)  // Nested field support
```

#### `notEquals(String field, Object value)`
Not equal comparison.

```java
.notEquals("status", Status.DELETED)
```

### String Operations

All string operations are **case-insensitive**.

#### `like(String field, String value)`
Contains search (adds `%` before and after).

```java
.like("name", "john")  // Matches "John Doe", "johnny", etc.
// SQL: WHERE LOWER(name) LIKE '%john%'
```

#### `startsWith(String field, String value)`
Prefix search.

```java
.startsWith("email", "admin")  // Matches "admin@example.com"
// SQL: WHERE LOWER(email) LIKE 'admin%'
```

#### `endsWith(String field, String value)`
Suffix search.

```java
.endsWith("email", "gmail.com")  // Matches "user@gmail.com"
// SQL: WHERE LOWER(email) LIKE '%gmail.com'
```

### Comparison Operations

#### `greaterThan(String field, Y value)`
Greater than comparison.

```java
.greaterThan("age", 18)
.greaterThan("createdAt", LocalDateTime.now().minusDays(7))
```

#### `greaterThanOrEqualTo(String field, Y value)`
Greater than or equal comparison.

```java
.greaterThanOrEqualTo("salary", 50000.0)
```

#### `lessThan(String field, Y value)`
Less than comparison.

```java
.lessThan("age", 65)
```

#### `lessThanOrEqualTo(String field, Y value)`
Less than or equal comparison.

```java
.lessThanOrEqualTo("price", 100.0)
```

### Range Operations

#### `between(String field, Y start, Y end)`
Range check (inclusive).

```java
.between("age", 18, 65)
.between("price", 10.0, 100.0)
```

### Null Checks

#### `isNull(String field)`
Check if field is null.

```java
.isNull("deletedAt")  // Find non-deleted records
```

#### `isNotNull(String field)`
Check if field is not null.

```java
.isNotNull("updatedAt")  // Find updated records
```

### Collection Operations

#### `in(String field, Collection<?> values)`
Check if field value is in a collection.

```java
.in("role", List.of(Role.ADMIN, Role.MODERATOR))
.in("status", Arrays.asList(Status.ACTIVE, Status.PENDING))
```

#### `notIn(String field, Collection<?> values)`
Check if field value is not in a collection.

```java
.notIn("status", List.of(Status.DELETED, Status.BANNED))
```

### Join Operations

For querying related entities (many-to-many or one-to-many relationships).

#### `joinEquals(String field, Object value)`
Inner join with equality check.

```java
// Find users with specific role name
.joinEquals("roles.name", "ADMIN")

// Multiple levels
.joinEquals("department.company.name", "Acme Corp")
```

#### `joinLike(String field, String value)`
Inner join with LIKE search.

```java
.joinLike("roles.name", "admin")  // Matches "ADMIN", "SUPER_ADMIN", etc.
```

#### `joinIn(String field, Collection<?> values)`
Inner join with IN clause.

```java
.joinIn("roles.name", List.of("ADMIN", "MODERATOR"))
```

#### `leftJoinEquals(String field, Object value)`
Left join with equality check.

```java
.leftJoinEquals("profiles.verified", true)
```

**Note:** Join operations automatically set `distinct(true)` to avoid duplicate results.

### Date Operations

#### `dateEquals(String field, LocalDate date)`
Exact date match.

```java
.dateEquals("birthDate", LocalDate.of(1990, 1, 1))
```

#### `dateBetween(String field, LocalDate start, LocalDate end)`
Date range check.

```java
LocalDate startDate = LocalDate.of(2024, 1, 1);
LocalDate endDate = LocalDate.of(2024, 12, 31);
.dateBetween("createdDate", startDate, endDate)
```

#### `dateTimeBetween(String field, LocalDateTime start, LocalDateTime end)`
DateTime range check.

```java
LocalDateTime start = LocalDateTime.now().minusDays(7);
LocalDateTime end = LocalDateTime.now();
.dateTimeBetween("createdAt", start, end)
```

### Logical Operations

#### `or(Specification<T> specification)`
Combines the last specification with another using OR.

```java
SpecificationBuilder.<User>builder()
    .equals("status", Status.ACTIVE)
    .or((root, query, cb) -> cb.equal(root.get("status"), Status.PENDING))
    .build();

// SQL: WHERE status = 'ACTIVE' OR status = 'PENDING'
```

### Custom Specifications

#### `custom(Specification<T> specification)`
Add a custom specification for complex conditions.

```java
.custom((root, query, cb) -> cb.isTrue(root.get("active")))
.custom((root, query, cb) -> {
    // Complex custom logic
    return cb.and(
        cb.equal(root.get("type"), "PREMIUM"),
        cb.greaterThan(root.get("score"), 100)
    );
})
```

## Advanced Examples

### Complex Search with Multiple Criteria

```java
@Service
public class UserService {
    
    public Page<User> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = SpecificationBuilder.<User>builder()
            .like("username", criteria.getUsername())
            .like("email", criteria.getEmail())
            .in("status", criteria.getStatuses())
            .greaterThanOrEqualTo("createdAt", criteria.getCreatedAfter())
            .lessThanOrEqualTo("createdAt", criteria.getCreatedBefore())
            .isNull("deletedAt")
            .build();
        
        return userRepository.findAll(spec, pageable);
    }
}
```

### Nested Field Access

```java
// Entity structure: User -> Address -> City
Specification<User> spec = SpecificationBuilder.<User>builder()
    .equals("address.city.name", "New York")
    .equals("address.zipCode", "10001")
    .build();
```

### Many-to-Many Relationships

```java
// Find users with specific roles
Specification<User> spec = SpecificationBuilder.<User>builder()
    .joinIn("roles.name", List.of("ADMIN", "SUPER_ADMIN"))
    .equals("status", Status.ACTIVE)
    .build();
```

### Date Range Queries

```java
// Find orders from last month
LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
LocalDate endOfMonth = LocalDate.now();

Specification<Order> spec = SpecificationBuilder.<Order>builder()
    .dateBetween("orderDate", startOfMonth, endOfMonth)
    .equals("status", OrderStatus.COMPLETED)
    .build();
```

### Complex OR Conditions

```java
Specification<Product> spec = SpecificationBuilder.<Product>builder()
    .equals("category", "Electronics")
    .greaterThan("price", 100.0)
    .or((root, query, cb) -> cb.lessThan(root.get("price"), 50.0))
    .isNotNull("stock")
    .build();

// SQL: WHERE category = 'Electronics' AND (price > 100 OR price < 50) AND stock IS NOT NULL
```

### Dynamic Query Building

```java
public Specification<Product> buildProductFilter(ProductFilter filter) {
    SpecificationBuilder<Product> builder = SpecificationBuilder.builder();
    
    if (filter.getName() != null) {
        builder.like("name", filter.getName());
    }
    
    if (filter.getCategory() != null) {
        builder.equals("category", filter.getCategory());
    }
    
    if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
        builder.between("price", filter.getMinPrice(), filter.getMaxPrice());
    }
    
    if (filter.getInStock() != null && filter.getInStock()) {
        builder.greaterThan("stockQuantity", 0);
    }
    
    return builder.build();
}
```

### Combining with Custom Specifications

```java
Specification<User> spec = SpecificationBuilder.<User>builder()
    .equals("status", Status.ACTIVE)
    .custom((root, query, cb) -> {
        // Custom logic: users with at least one order
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Order> orderRoot = subquery.from(Order.class);
        subquery.select(cb.count(orderRoot.get("id")))
                .where(cb.equal(orderRoot.get("user"), root));
        return cb.greaterThan(subquery, 0L);
    })
    .build();
```

### Search Across Multiple Fields

```java
public Specification<User> searchByKeyword(String keyword) {
    return SpecificationBuilder.<User>builder()
        .like("username", keyword)
        .or((root, query, cb) -> cb.like(
            cb.lower(root.get("email")), 
            "%" + keyword.toLowerCase() + "%"
        ))
        .or((root, query, cb) -> cb.like(
            cb.lower(root.get("firstName")), 
            "%" + keyword.toLowerCase() + "%"
        ))
        .or((root, query, cb) -> cb.like(
            cb.lower(root.get("lastName")), 
            "%" + keyword.toLowerCase() + "%"
        ))
        .build();
}
```

## Best Practices

### 1. Null Safety
The builder automatically handles null values - they won't be added to the specification:

```java
// Safe even if email is null
.like("email", searchCriteria.getEmail())  // Ignored if null
```

### 2. Empty Collections
Empty collections are also safely ignored:

```java
// Safe even if statuses is empty
.in("status", searchCriteria.getStatuses())  // Ignored if empty
```

### 3. Type Safety
Use generics for type safety:

```java
SpecificationBuilder<User> builder = SpecificationBuilder.<User>builder();
```

### 4. Reusable Specifications
Create reusable specification methods:

```java
public class UserSpecifications {
    
    public static Specification<User> isActive() {
        return SpecificationBuilder.<User>builder()
            .equals("status", Status.ACTIVE)
            .isNull("deletedAt")
            .build();
    }
    
    public static Specification<User> hasRole(String roleName) {
        return SpecificationBuilder.<User>builder()
            .joinEquals("roles.name", roleName)
            .build();
    }
}

// Usage
Specification<User> spec = Specification
    .where(UserSpecifications.isActive())
    .and(UserSpecifications.hasRole("ADMIN"));
```

### 5. Indexing
Ensure database indexes exist for frequently queried fields:

```java
// If you frequently query by status and createdAt
.equals("status", Status.ACTIVE)
.greaterThan("createdAt", someDate)

// Consider adding a composite index: (status, createdAt)
```

### 6. Join Performance
Be aware that join operations can impact performance. Use them judiciously:

```java
// Prefer fetch joins for N+1 problems
@EntityGraph(attributePaths = {"roles"})
List<User> findAll(Specification<User> spec);
```

### 7. Pagination
Always use pagination for potentially large result sets:

```java
Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
Page<User> results = userRepository.findAll(spec, pageable);
```

### 8. Testing
Write tests for your specifications:

```java
@Test
void testUserSearchSpecification() {
    Specification<User> spec = SpecificationBuilder.<User>builder()
        .equals("status", Status.ACTIVE)
        .like("email", "test")
        .build();
    
    List<User> results = userRepository.findAll(spec);
    
    assertThat(results).allMatch(user -> 
        user.getStatus() == Status.ACTIVE &&
        user.getEmail().contains("test")
    );
}
```

## Repository Integration

### Basic Repository

```java
public interface UserRepository extends JpaRepository<User, Long>, 
                                       JpaSpecificationExecutor<User> {
}
```

### Service Layer Example

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public Page<User> findUsers(UserSearchDTO searchDTO, Pageable pageable) {
        Specification<User> spec = SpecificationBuilder.<User>builder()
            .like("username", searchDTO.getUsername())
            .like("email", searchDTO.getEmail())
            .in("status", searchDTO.getStatuses())
            .dateBetween("createdDate", searchDTO.getStartDate(), searchDTO.getEndDate())
            .isNull("deletedAt")
            .build();
        
        return userRepository.findAll(spec, pageable);
    }
    
    public long countActiveUsers() {
        Specification<User> spec = SpecificationBuilder.<User>builder()
            .equals("status", Status.ACTIVE)
            .isNull("deletedAt")
            .build();
        
        return userRepository.count(spec);
    }
    
    public boolean exists(String username) {
        Specification<User> spec = SpecificationBuilder.<User>builder()
            .equals("username", username)
            .build();
        
        return userRepository.exists(spec);
    }
}
```

## Common Patterns

### Soft Delete Pattern

```java
private Specification<User> notDeleted() {
    return SpecificationBuilder.<User>builder()
        .isNull("deletedAt")
        .build();
}

// Use in all queries
public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(notDeleted(), pageable);
}
```

### Multi-Tenant Pattern

```java
private Specification<Entity> forTenant(Long tenantId) {
    return SpecificationBuilder.<Entity>builder()
        .equals("tenantId", tenantId)
        .build();
}
```

### Active Records Pattern

```java
private Specification<User> activeUsers() {
    return SpecificationBuilder.<User>builder()
        .equals("status", Status.ACTIVE)
        .equals("enabled", true)
        .isNull("deletedAt")
        .build();
}
```

## Troubleshooting

### Issue: Duplicate Results with Joins

**Solution:** Join operations automatically call `distinct(true)`, but if you're still seeing duplicates:

```java
// Use distinct in your query method
@Query("SELECT DISTINCT u FROM User u")
List<User> findAll(Specification<User> spec);
```

### Issue: Performance Problems

**Solution:** 
- Add database indexes on frequently queried fields
- Use pagination
- Consider using native queries for complex scenarios
- Profile your queries with EXPLAIN

### Issue: Nested Field Not Found

**Solution:** Ensure the field path is correct and relationships are properly mapped:

```java
// Correct: use the actual field names from entities
.equals("address.city.name", "New York")

// Not: database column names
```

---

## License

This utility is part of the ecommerce project.

## Support

For issues or questions, please contact the development team or create an issue in the project repository.
