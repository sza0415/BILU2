package org.sza.bilu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页结果DTO
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "总页数")
    private long pages;

    @Schema(description = "当前页码")
    private long current;
    
    @Schema(description = "每页记录数")
    private long size;

    @Schema(description = "页面数据")
    private List<T> records;

    @Schema(description = "额外数据")
    private Object extra;

    /**
     * 创建分页结果对象
     *
     * @param total 总记录数
     * @param pages 总页数
     * @param current 当前页码
     * @param size 每页记录数
     * @param records 页面数据
     * @param <T> 数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(long total, long pages, long current, long size, List<T> records) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .current(current)
                .size(size)
                .records(records)
                .build();
    }

    /**
     * 创建分页结果对象（不带size参数，向后兼容）
     *
     * @param total 总记录数
     * @param pages 总页数
     * @param current 当前页码
     * @param records 页面数据
     * @param <T> 数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(long total, long pages, long current, List<T> records) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .current(current)
                .size(10) // 设置默认每页记录数为10
                .records(records)
                .build();
    }

    /**
     * 创建分页结果对象（带额外数据）
     *
     * @param total 总记录数
     * @param pages 总页数
     * @param current 当前页码
     * @param size 每页记录数
     * @param records 页面数据
     * @param extra 额外数据
     * @param <T> 数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(long total, long pages, long current, long size, List<T> records, Object extra) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .current(current)
                .size(size)
                .records(records)
                .extra(extra)
                .build();
    }
    
    /**
     * 创建分页结果对象（带额外数据，不带size参数，向后兼容）
     *
     * @param total 总记录数
     * @param pages 总页数
     * @param current 当前页码
     * @param records 页面数据
     * @param extra 额外数据
     * @param <T> 数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(long total, long pages, long current, List<T> records, Object extra) {
        return PageResult.<T>builder()
                .total(total)
                .pages(pages)
                .current(current)
                .size(10) // 设置默认每页记录数为10
                .records(records)
                .extra(extra)
                .build();
    }
}