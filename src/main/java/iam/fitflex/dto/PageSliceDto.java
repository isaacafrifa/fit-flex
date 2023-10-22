package iam.fitflex.dto;

import java.util.List;

public record PageSliceDto(
         List<Object> content,
         int pageSize,
         int pageNumber,
         long numberOfElements
) {
}
