package com.demoJob.demo.dto.response.system;

import lombok.Builder;
import lombok.Getter;
import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class PageResponse<T> implements Serializable {
    private int page;
    private int size;
    private long total;
    private List<T> items;
}
