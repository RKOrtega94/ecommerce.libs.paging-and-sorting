package ec.com.ecommerce.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public class PaginationUtil {
    private PaginationUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private static final String PAGE_PARAM = "page"; // 1-based index
    private static final String SIZE_PARAM = "size"; // items per page
    private static final String SORT_PARAM = "sort"; // e.g., "name,asc" or "name,desc"

    public static Pageable pageable(Map<String, Object> params) {
        int page = params.get(PAGE_PARAM) != null ? Integer.parseInt(params.get(PAGE_PARAM).toString()) : DEFAULT_PAGE;
        int size = params.get(SIZE_PARAM) != null ? Integer.parseInt(params.get(SIZE_PARAM).toString()) : DEFAULT_SIZE;

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        Sort sort = Sort.unsorted();
        if (params.get(SORT_PARAM) != null) {
            String[] sortParams = params.get(SORT_PARAM).toString().split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sort = Sort.by(direction, property);
            }
        }

        if (page < 1) page = DEFAULT_PAGE;

        return PageRequest.of(page - 1, size, sort);
    }
}
