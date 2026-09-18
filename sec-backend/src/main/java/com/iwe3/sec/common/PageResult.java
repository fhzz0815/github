package com.iwe3.sec.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    // 总记录数
    private Long total;

    // 总页数
    private Integer pages;

    // 当前页码
    private Integer page;

    // 每页条数
    private Integer size;

    // 当前页数据
    private List<T> list;

    public static <T> PageResult<T> of(Long total, Integer pages, List<T> list, Integer page, Integer size) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .page(page)
                .size(size)
                .list(list)
                .build();
    }

    public static <T> PageResult<T> of(Long total, Integer pages, List<T> list) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .list(list)
                .build();
    }
}
