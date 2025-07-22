package org.sza.bilu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用返回结果DTO
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用返回结果")
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "数据")
    private T data;

    /**
     * 成功结果（无数据）
     */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /**
     * 成功结果（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /**
     * 成功结果（带消息和数据）
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(200, message, data);
    }

    /**
     * 失败结果（带消息）
     */
    public static <T> R<T> fail(String message) {
        return new R<>(400, message, null);
    }

    /**
     * 失败结果（带状态码和消息）
     */
    public static <T> R<T> fail(String message, int code) {
        return new R<>(code, message, null);
    }
    
    /**
     * 失败结果（带状态码、消息和数据）
     */
    public static <T> R<T> fail(String message, int code, T data) {
        return new R<>(code, message, data);
    }
}