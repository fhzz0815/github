package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;
import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.entity.MemberPointsRecordEntity;
import com.iwe3.sec.mapper.MemberBalanceRecordMapper;
import com.iwe3.sec.mapper.MemberMapper;
import com.iwe3.sec.mapper.MemberPointsRecordMapper;
import com.iwe3.sec.service.IMemberWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员钱包服务实现类
 * 统一管理余额和积分的增减，保证「改主档 + 写流水」在同一个事务里完成
 * 业务方不要再直接调用 memberMapper.update() 改余额/积分，都走这里
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberWalletServiceImpl implements IMemberWalletService {

    private final MemberMapper memberMapper;
    private final MemberBalanceRecordMapper memberBalanceRecordMapper;
    private final MemberPointsRecordMapper memberPointsRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal rechargeBalance(Long memberId, BigDecimal amount, BigDecimal giftAmount,
                                      String sourceType, Long sourceId, String remark) {
        // 先查到当前余额，用于计算变动后的余额
        MemberEntity member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会员不存在");
        }

        // 原子增加余额（用 SQL 里的 balance = balance + #{amount} ，防止并发覆盖）
        int affected = memberMapper.increaseBalance(memberId, amount.add(giftAmount));
        if (affected == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "充值失败，请稍后重试");
        }

        // 计算变动后余额（当前余额 + 充值金额 + 赠送金额）
        BigDecimal balanceAfter = member.getBalance().add(amount).add(giftAmount);

        // 写入余额流水
        MemberBalanceRecordEntity record = MemberBalanceRecordEntity.builder()
                .memberId(memberId)
                .changeAmount(amount.add(giftAmount))
                .balanceAfter(balanceAfter)
                .changeType("RECHARGE")
                .sourceType(sourceType)
                .sourceId(sourceId)
                .remark(remark)
                .build();
        memberBalanceRecordMapper.insert(record);

        log.info("会员充值成功：memberId={}, amount={}, giftAmount={}, balanceAfter={}",
                memberId, amount, giftAmount, balanceAfter);
        return balanceAfter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal deductBalance(Long memberId, BigDecimal amount,
                                    String sourceType, Long sourceId, String remark) {
        MemberEntity member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会员不存在");
        }

        // 原子扣减余额（SQL 里加了 balance >= #{amount} 条件，余额不够时影响行数为 0）
        int affected = memberMapper.decreaseBalance(memberId, amount);
        if (affected == 0) {
            log.warn("会员余额不足：memberId={}, balance={}, deductAmount={}",
                    memberId, member.getBalance(), amount);
            return null;
        }

        BigDecimal balanceAfter = member.getBalance().subtract(amount);

        // 写入余额流水（变动金额为负数表示支出）
        MemberBalanceRecordEntity record = MemberBalanceRecordEntity.builder()
                .memberId(memberId)
                .changeAmount(amount.negate())
                .balanceAfter(balanceAfter)
                .changeType("CONSUME")
                .sourceType(sourceType)
                .sourceId(sourceId)
                .remark(remark)
                .build();
        memberBalanceRecordMapper.insert(record);

        log.info("会员余额扣减成功：memberId={}, amount={}, balanceAfter={}",
                memberId, amount, balanceAfter);
        return balanceAfter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer earnPoints(Long memberId, Integer points,
                              String sourceType, Long sourceId, String remark) {
        MemberEntity member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会员不存在");
        }

        // 原子增加总积分和可用积分
        memberMapper.increaseTotalAndAvailablePoints(memberId, points);

        int pointsAfter = (member.getAvailablePoints() != null ? member.getAvailablePoints() : 0) + points;

        // 写入积分流水
        MemberPointsRecordEntity record = MemberPointsRecordEntity.builder()
                .memberId(memberId)
                .pointsChange(points)
                .pointsAfter(pointsAfter)
                .changeType("CONSUME")
                .sourceType(sourceType)
                .sourceId(sourceId)
                .remark(remark)
                .build();
        memberPointsRecordMapper.insert(record);

        log.info("会员增加积分：memberId={}, points={}, pointsAfter={}", memberId, points, pointsAfter);
        return pointsAfter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deductPoints(Long memberId, Integer points,
                                String sourceType, Long sourceId, String remark) {
        MemberEntity member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会员不存在");
        }

        // 原子扣减可用积分（SQL 里加了 available_points >= #{points} 条件）
        int affected = memberMapper.decreaseAvailablePoints(memberId, points);
        if (affected == 0) {
            log.warn("会员积分不足：memberId={}, availablePoints={}, deductPoints={}",
                    memberId, member.getAvailablePoints(), points);
            return null;
        }

        int pointsAfter = (member.getAvailablePoints() != null ? member.getAvailablePoints() : 0) - points;

        // 写入积分流水（变动为负数）
        MemberPointsRecordEntity record = MemberPointsRecordEntity.builder()
                .memberId(memberId)
                .pointsChange(-points)
                .pointsAfter(pointsAfter)
                .changeType("EXCHANGE")
                .sourceType(sourceType)
                .sourceId(sourceId)
                .remark(remark)
                .build();
        memberPointsRecordMapper.insert(record);

        log.info("会员扣减积分：memberId={}, points={}, pointsAfter={}", memberId, points, pointsAfter);
        return pointsAfter;
    }
}
