package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;
import com.iwe3.sec.common.JwtUtil;
import com.iwe3.sec.entity.StaffLoginLogEntity;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.StaffLoginLogMapper;
import com.iwe3.sec.mapper.SysUserMapper;
import com.iwe3.sec.service.IAuthService;
import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 认证业务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    // 员工数据访问接口
    private final SysUserMapper sysUserMapper;
    // JWT工具类，用来生成和解析令牌
    private final JwtUtil jwtUtil;
    // 登录日志数据访问接口
    private final StaffLoginLogMapper staffLoginLogMapper;
    // bcrypt加密工具，兼容bcrypt加密的密码
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(String username, String password) {
        // 记录当前时间，用在整个方法中保持时间一致
        Date now = new Date();

        // 查找用户信息
        SysUserEntity user = getByUsername(username);

        // ========== 各种登录失败的校验 ==========
        if (user == null) {
            // 账号不存在：记录失败日志后抛异常
            log.warn("登录失败：账号不存在，username={}", username);
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "账号不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            writeLoginLog(user.getId(), now, 0, "账号已被禁用");
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "账号已被禁用");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            writeLoginLog(user.getId(), now, 0, "账号已被停用");
            throw new BusinessException(ErrorCode.ACCOUNT_SUSPENDED, "账号已被停用");
        }

        // ========== 密码校验：优先使用 bcrypt ==========
        boolean passwordMatch = false;
        String storedPassword = user.getPassword();

        if (storedPassword != null && storedPassword.startsWith("$2")) {
            // 密码已升级为 bcrypt，直接使用 bcrypt 比对
            passwordMatch = passwordEncoder.matches(password, storedPassword);
        } else {
            // 密码仍是 MD5 存储（旧版本兼容）
            String md5Password = SecureUtil.md5(password);
            passwordMatch = md5Password.equalsIgnoreCase(storedPassword);

            if (passwordMatch) {
                // 登录成功后自动升级为 bcrypt 加密，提高安全性
                String bcryptPassword = passwordEncoder.encode(password);
                sysUserMapper.updatePassword(user.getId(), bcryptPassword);
                log.info("密码已从 MD5 升级为 bcrypt：userId={}", user.getId());
            }
        }

        if (!passwordMatch) {
            // 密码错误：记录失败日志
            writeLoginLog(user.getId(), now, 0, "密码错误");
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "密码错误");
        }

        // ========== 登录成功：更新最后登录时间 + 写入登录日志 ==========
        try {
            sysUserMapper.updateLastLoginTime(user.getId(), now);
        } catch (Exception e) {
            // 更新登录时间失败不应阻断登录流程，仅记录日志
            log.warn("更新最后登录时间失败：userId={}, err={}", user.getId(), e.getMessage());
        }

        // 写入登录成功日志（登录失败的情况在上面已分别记录）
        writeLoginLog(user.getId(), now, 1, null);

        log.info("用户登录成功：userId={}, username={}", user.getId(), username);
        return jwtUtil.generateAccessToken(user.getId(), user.getUsername());
    }

    /**
     * 写入登录日志
     *
     * @param staffId    员工ID
     * @param loginTime  登录时间
     * @param result     登录结果（1=成功，0=失败）
     * @param failReason 失败原因（成功时传 null）
     */
    private void writeLoginLog(Long staffId, Date loginTime, Integer result, String failReason) {
        try {
            StaffLoginLogEntity logEntity = StaffLoginLogEntity.builder()
                    .staffId(staffId)
                    .loginTime(loginTime)
                    .loginResult(result)
                    .failReason(failReason)
                    .build();
            staffLoginLogMapper.insert(logEntity);
        } catch (Exception e) {
            // 写入登录日志失败不应阻断登录流程，仅记录日志
            log.warn("写入登录日志失败：staffId={}, err={}", staffId, e.getMessage());
        }
    }

    @Override
    public SysUserEntity getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }
}
