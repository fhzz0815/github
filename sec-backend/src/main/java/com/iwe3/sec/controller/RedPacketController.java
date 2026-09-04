package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IRedPacketService;
import com.iwe3.sec.entity.RedPacketEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * red_packet 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/redPackets")
public class RedPacketController {

    private final IRedPacketService redPacketService;

    public RedPacketController(IRedPacketService redPacketService) {
        this.redPacketService = redPacketService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<RedPacketEntity>> list(RedPacketEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(redPacketService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<RedPacketEntity> getById(@PathVariable Long id) {
        return Result.success(redPacketService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody RedPacketEntity entity) {
        redPacketService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RedPacketEntity entity) {
        entity.setId(id);
        redPacketService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        redPacketService.remove(id);
        return Result.success();
    }
}
