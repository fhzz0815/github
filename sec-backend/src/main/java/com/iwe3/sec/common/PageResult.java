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

    // 当前页数据
    private List<T> list;

    public static <T> PageResult<T> of(Long total, Integer pages, List<T> list) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .list(list)
                .build();
    }
}
