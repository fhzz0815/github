package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.mapper.MemberMapper;
import com.iwe3.sec.service.IMemberService;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.cache.CacheHelper;
import com.iwe3.sec.common.cache.CacheKey;

import java.util.List;

/**
 * member 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店会员，总店长可看全部
 */
@Service
public class MemberServiceImpl implements IMemberService {

    private final MemberMapper memberMapper;
    private final PermissionChecker permissionChecker;
    private final CacheHelper cacheHelper;

    public MemberServiceImpl(MemberMapper memberMapper,
                             PermissionChecker permissionChecker,
                             CacheHelper cacheHelper) {
        this.memberMapper = memberMapper;
        this.permissionChecker = permissionChecker;
        this.cacheHelper = cacheHelper;
    }

    @Override
    public PageResult<MemberEntity> list(MemberEntity query, Integer page, Integer size) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(403, "无门店归属，无法查看会员");
            }
            query.setRegisterStoreId(myStoreId);
        }
        PageHelper.startPage(page, size);
        List<MemberEntity> list = memberMapper.selectList(query);
        PageInfo<MemberEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberEntity getById(Long id) {
        // 用缓存查会员信息，缓存没命中再查数据库
        return cacheHelper.getOrLoad(CacheKey.PREFIX_MEMBER + id, CacheKey.TTL_MEMBER, MemberEntity.class, () -> {
            MemberEntity m = memberMapper.selectById(id);
            if (m != null) {
                permissionChecker.assertInOwnStore(m.getRegisterStoreId());
            }
            return m;
        });
    }

    @Override
    public boolean add(MemberEntity entity) {
        permissionChecker.setStoreIdIfNeeded(entity::setRegisterStoreId);
        boolean result = memberMapper.insert(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_MEMBER + entity.getId());
        }
        return result;
    }

    @Override
    public boolean update(MemberEntity entity) {
        if (entity.getId() != null) {
            MemberEntity existing = memberMapper.selectById(entity.getId());
            if (existing != null) {
                permissionChecker.assertInOwnStore(existing.getRegisterStoreId());
            }
        }
        boolean result = memberMapper.update(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_MEMBER + entity.getId());
        }
        return result;
    }

    @Override
    public boolean remove(Long id) {
        MemberEntity existing = memberMapper.selectById(id);
        if (existing != null) {
            permissionChecker.assertInOwnStore(existing.getRegisterStoreId());
        }
        boolean result = memberMapper.deleteById(id) > 0;
        if (result) {
            cacheHelper.delete(CacheKey.PREFIX_MEMBER + id);
        }
        return result;
    }

    // 注意：assertInOwnStore() 已统一抽取到 PermissionChecker 中
}
