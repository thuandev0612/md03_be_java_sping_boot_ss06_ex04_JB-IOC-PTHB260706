package com.example.baitapsession06.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private final List<T> items;
    private final Integer page;
    private final Integer size;
    private final Long totalItems;
    private final Integer totalPages;
    private final Boolean isLast;
}
