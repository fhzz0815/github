package com.iwe3.sec.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 更新菜品制作状态请求参数
 */
public class UpdateMakeStatusRequest {

    private Long detailId;

    @NotNull(message = "制作状态不能为空")
    private Integer makeStatus;

    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public Integer getMakeStatus() { return makeStatus; }
    public void setMakeStatus(Integer makeStatus) { this.makeStatus = makeStatus; }
}
