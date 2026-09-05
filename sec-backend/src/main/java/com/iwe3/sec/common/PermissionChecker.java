package com.iwe3.sec.common;

import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysRoleMapper;
import com.iwe3.sec.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 权限校验工具：所有业务层通过此类获取当前登录人信息和做权限判断
 * 简单手动校验，不使用 AOP，便于排查
 */
@Component
@RequiredArgsConstructor
public class PermissionChecker {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    // 总店长等级阈值
    public static final int LEVEL_GENERAL_MANAGER = 99;
    // 店长等级阈值
    public static final int LEVEL_STORE_MANAGER = 50;

    /** 取当前登录人 */
    public LoginUser current() {
        HttpServletRequest req =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        LoginUser user = (LoginUser) req.getAttribute("loginUser");
        if (user == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return user;
    }

    public Long currentUserId() {
        return current().getUserId();
    }

    public Long currentStoreId() {
        return current().getStoreId();
    }

    public Integer currentRoleLevel() {
        return current().getRoleLevel();
    }

    /** 仅总店长可通过 */
    public void assertGeneralManager() {
        if (currentRoleLevel() == null || currentRoleLevel() < LEVEL_GENERAL_MANAGER) {
            throw new BusinessException(403, "无权限，仅总店长可操作");
        }
    }

    /** 判断当前用户能否管理（增改删）目标员工 */
    public void assertCanManageUser(SysUserEntity target) {
        if (target == null) {
            throw new BusinessException(1001, "目标员工不存在");
        }
        LoginUser me = current();
        // 自己改自己：放行（profile 自助编辑场景）
        if (Objects.equals(me.getUserId(), target.getId())) {
            return;
        }
        // 总店长：全国都可管理
        if (me.getRoleLevel() != null && me.getRoleLevel() >= LEVEL_GENERAL_MANAGER) {
            return;
        }
        // 店长：仅本门店且目标等级严格低于自己
        if (me.getRoleLevel() != null && me.getRoleLevel() >= LEVEL_STORE_MANAGER) {
            if (me.getStoreId() == null || !me.getStoreId().equals(target.getStoreId())) {
                throw new BusinessException(403, "无权限，只能管理本门店员工");
            }
            Integer targetLevel = getRoleLevel(target.getRoleId());
            if (targetLevel == null || targetLevel >= me.getRoleLevel()) {
                throw new BusinessException(403, "无权限，不能操作同级或上级员工");
            }
            return;
        }
        // 普通员工：不能管理任何人
        throw new BusinessException(403, "无权限，仅可查看同级基本信息");
    }

    /** 判断当前用户能否查看目标员工详情 */
    public void assertCanViewUserDetail(SysUserEntity target) {
        if (target == null) {
            throw new BusinessException(1001, "员工不存在");
        }
        LoginUser me = current();
        // 自己看自己：允许
        if (Objects.equals(me.getUserId(), target.getId())) {
            return;
        }
        // 总店长：看所有人
        if (me.getRoleLevel() != null && me.getRoleLevel() >= LEVEL_GENERAL_MANAGER) {
            return;
        }
        // 店长：本门店所有人 + 跨门店同级
        if (me.getRoleLevel() != null && me.getRoleLevel() >= LEVEL_STORE_MANAGER) {
            if (Objects.equals(target.getStoreId(), me.getStoreId())) {
                return;
            }
            Integer targetLevel = getRoleLevel(target.getRoleId());
            if (targetLevel != null && targetLevel.equals(me.getRoleLevel())) {
                return;
            }
            throw new BusinessException(403, "无权限查看该员工详情");
        }
        // 普通员工：仅同级（跨门店）
        Integer targetLevel = getRoleLevel(target.getRoleId());
        if (targetLevel != null && targetLevel.equals(me.getRoleLevel())) {
            return;
        }
        throw new BusinessException(403, "无权限查看上级员工详情");
    }

    /** 构建员工列表查询的数据范围 */
    public UserDataScope buildUserDataScope() {
        LoginUser me = current();
        if (me.getRoleLevel() == null) {
            throw new BusinessException(403, "当前用户角色等级缺失");
        }
        if (me.getRoleLevel() >= LEVEL_GENERAL_MANAGER) {
            return UserDataScope.builder().allStores(true).build();
        }
        if (me.getRoleLevel() >= LEVEL_STORE_MANAGER) {
            // 店长：本门店全部员工 + 跨门店同级
            return UserDataScope.builder()
                    .storeId(me.getStoreId())
                    .currentLevel(me.getRoleLevel())
                    .includeSameLevelCrossStore(true)
                    .build();
        }
        // 普通员工：仅同级（跨门店）
        return UserDataScope.builder()
                .levelOnly(me.getRoleLevel())
                .build();
    }

    /** 给"非管理者看到的目标员工"做敏感字段脱敏 */
    public void maskBasicInfo(SysUserEntity u) {
        if (u == null) {
            return;
        }
        // 仅保留：id, realName, staffNo, phone, roleId, storeId, roleLevel, roleName, storeName
        u.setPassword(null);
        u.setEmail(null);
        u.setIdCard(null);
        u.setLastLoginTime(null);
        u.setCreateTime(null);
        u.setUpdateTime(null);
        u.setStatus(null);
        u.setUsername(null);
        u.setAvatar(null);
    }

    /** 是否需要给目标员工做脱敏（用于列表） */
    public boolean shouldMaskTarget(SysUserEntity target) {
        LoginUser me = current();
        if (me.getRoleLevel() == null) {
            return true;
        }
        if (me.getRoleLevel() >= LEVEL_GENERAL_MANAGER) {
            return false;
        }
        if (me.getRoleLevel() >= LEVEL_STORE_MANAGER) {
            // 本门店：不脱敏；跨门店同级：脱敏
            return !Objects.equals(target.getStoreId(), me.getStoreId());
        }
        // 普通员工：所有都脱敏
        return true;
    }

    /** 通过 roleId 取等级（可后续加缓存） */
    private Integer getRoleLevel(Long roleId) {
        if (roleId == null) {
            return null;
        }
        SysRoleEntity r = sysRoleMapper.selectById(roleId);
        return r == null ? null : r.getLevel();
    }
}
