package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.JwtUtil;
import com.iwe3.sec.common.LoginUser;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.UserDataScope;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysUserMapper;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 员工管理业务类的单元测试
 * 使用 Mockito 模拟 Mapper 层和权限校验组件，只测试业务逻辑的正确性
 */
@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PermissionChecker permissionChecker;

    private SysUserServiceImpl sysUserService;

    /** 模拟总店长登录人，便于大多数测试复用 */
    private static final LoginUser GM = LoginUser.builder()
            .userId(999L)
            .storeId(null)
            .roleId(1L)
            .roleLevel(99)
            .roleCode("GENERAL_MANAGER")
            .username("gm")
            .build();

    @BeforeEach
    void setUp() {
        sysUserService = new SysUserServiceImpl(sysUserMapper, jwtUtil, permissionChecker);
    }

    @Test
    @DisplayName("测试分页查询列表 - 返回数据时密码应被清空")
    void testList_ShouldMaskPassword() {
        // 准备模拟数据
        SysUserEntity user1 = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").password("hashed123").build();
        SysUserEntity user2 = SysUserEntity.builder()
                .id(2L).username("lisi").realName("李四").password("hashed456").build();
        List<SysUserEntity> mockList = Arrays.asList(user1, user2);

        // 模拟权限：总店长范围、不需要脱敏
        when(permissionChecker.buildUserDataScope())
                .thenReturn(UserDataScope.builder().allStores(true).build());
        when(permissionChecker.shouldMaskTarget(any())).thenReturn(false);
        when(sysUserMapper.selectListByDataScope(any(), any())).thenReturn(mockList);

        // 执行查询
        PageResult<SysUserEntity> result = sysUserService.list(new SysUserEntity(), 1, 10);

        // 验证：返回的列表不为空，且密码字段被清空
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        result.getList().forEach(user -> assertNull(user.getPassword(), "列表返回时密码必须为空"));
    }

    @Test
    @DisplayName("测试分页查询列表 - 无数据时返回空列表")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(permissionChecker.buildUserDataScope())
                .thenReturn(UserDataScope.builder().allStores(true).build());
        when(sysUserMapper.selectListByDataScope(any(), any())).thenReturn(Collections.emptyList());

        PageResult<SysUserEntity> result = sysUserService.list(new SysUserEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("测试分页查询列表 - 非管理者查看时对目标行做基本字段脱敏")
    void testList_WhenNotManager_ShouldMaskBasicInfo() {
        SysUserEntity user1 = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三")
                .password("hashed123").email("a@b.com").idCard("110").build();
        List<SysUserEntity> mockList = Arrays.asList(user1);

        when(permissionChecker.buildUserDataScope())
                .thenReturn(UserDataScope.builder().levelOnly(10).build());
        when(permissionChecker.shouldMaskTarget(any())).thenReturn(true);
        // 调用真实脱敏方法，让字段真正被清空
        doCallRealMethod().when(permissionChecker).maskBasicInfo(any());
        when(sysUserMapper.selectListByDataScope(any(), any())).thenReturn(mockList);

        PageResult<SysUserEntity> result = sysUserService.list(new SysUserEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getList().size());
        SysUserEntity u = result.getList().get(0);
        assertNull(u.getPassword(), "密码必须清空");
        assertNull(u.getEmail(), "邮箱应被脱敏");
        assertNull(u.getIdCard(), "身份证应被脱敏");
        // 真实姓名保留
        assertEquals("张三", u.getRealName());
    }

    @Test
    @DisplayName("测试根据ID查询 - 用户存在时返回用户且密码为空")
    void testGetById_WhenUserExists_ShouldReturnUserWithoutPassword() {
        SysUserEntity mockUser = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").password("hashed123").build();
        when(sysUserMapper.selectById(1L)).thenReturn(mockUser);
        // 权限校验通过、不脱敏
        doNothing().when(permissionChecker).assertCanViewUserDetail(any());
        when(permissionChecker.shouldMaskTarget(any())).thenReturn(false);

        SysUserEntity result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());
        assertNull(result.getPassword(), "详情返回时密码必须为空");
    }

    @Test
    @DisplayName("测试根据ID查询 - 用户不存在时抛出业务异常")
    void testGetById_WhenUserNotExists_ShouldThrowException() {
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.getById(999L));

        assertEquals(1001, exception.getCode());
    }

    @Test
    @DisplayName("测试根据ID查询 - 无权限查看时抛出403异常")
    void testGetById_WhenNoPermission_ShouldThrow403() {
        SysUserEntity mockUser = SysUserEntity.builder()
                .id(1L).username("zhangsan").build();
        when(sysUserMapper.selectById(1L)).thenReturn(mockUser);
        // 模拟权限校验失败
        doThrow(new BusinessException(403, "无权限查看该员工详情"))
                .when(permissionChecker).assertCanViewUserDetail(any());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.getById(1L));

        assertEquals(403, exception.getCode());
    }

    @Test
    @DisplayName("测试新增用户 - 有密码时进行MD5加密后入库")
    void testAdd_WithPassword_ShouldEncryptPassword() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").realName("新用户").password("123456").build();
        // 权限校验通过、当前登录人为总店长
        doNothing().when(permissionChecker).assertCanManageUser(any());
        when(permissionChecker.current()).thenReturn(GM);

        sysUserService.add(newUser);

        // 验证密码被MD5加密
        String expectedPwd = SecureUtil.md5("123456");
        assertEquals(expectedPwd, newUser.getPassword(), "密码应被MD5加密");
        verify(sysUserMapper).insert(newUser);
    }

    @Test
    @DisplayName("测试新增用户 - 未填密码时使用默认密码123456")
    void testAdd_WithoutPassword_ShouldUseDefaultPassword() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").realName("新用户").build();
        doNothing().when(permissionChecker).assertCanManageUser(any());
        when(permissionChecker.current()).thenReturn(GM);

        sysUserService.add(newUser);

        // 验证默认密码被MD5加密
        String expectedPwd = SecureUtil.md5("123456");
        assertEquals(expectedPwd, newUser.getPassword(), "未填密码时应使用默认密码123456的MD5值");
        verify(sysUserMapper).insert(newUser);
    }

    @Test
    @DisplayName("测试新增用户 - 店长新增时强制storeId为本人门店")
    void testAdd_WhenStoreManager_ShouldForceOwnStoreId() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").realName("新用户").password("123456")
                .storeId(999L) // 故意填别的门店
                .build();
        LoginUser storeManager = LoginUser.builder()
                .userId(50L).storeId(10L).roleId(2L).roleLevel(50).build();
        doNothing().when(permissionChecker).assertCanManageUser(any());
        when(permissionChecker.current()).thenReturn(storeManager);

        sysUserService.add(newUser);

        // 店长新增员工时 storeId 应被强制改成本人门店
        assertEquals(10L, newUser.getStoreId(), "店长新增员工时storeId应被强制为本人门店");
        verify(sysUserMapper).insert(newUser);
    }

    @Test
    @DisplayName("测试新增用户 - 无权限时抛出403异常")
    void testAdd_WhenNoPermission_ShouldThrow403() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").password("123456").build();
        doThrow(new BusinessException(403, "无权限，仅可查看同级基本信息"))
                .when(permissionChecker).assertCanManageUser(any());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.add(newUser));

        assertEquals(403, exception.getCode());
        verify(sysUserMapper, never()).insert(any());
    }

    @Test
    @DisplayName("测试修改用户 - 同时修改密码时进行MD5加密")
    void testUpdate_WithPassword_ShouldEncryptPassword() {
        SysUserEntity existing = SysUserEntity.builder()
                .id(1L).username("olduser").storeId(1L).roleId(2L).build();
        SysUserEntity updateUser = SysUserEntity.builder()
                .id(1L).realName("新名字").password("newpwd123").build();
        when(sysUserMapper.selectById(1L)).thenReturn(existing);
        doNothing().when(permissionChecker).assertCanManageUser(any());

        sysUserService.update(updateUser);

        String expectedPwd = SecureUtil.md5("newpwd123");
        assertEquals(expectedPwd, updateUser.getPassword(), "修改密码时需MD5加密");
        verify(sysUserMapper).update(updateUser);
    }

    @Test
    @DisplayName("测试修改用户 - 未修改密码时不应加密")
    void testUpdate_WithoutPassword_ShouldNotEncrypt() {
        SysUserEntity existing = SysUserEntity.builder()
                .id(1L).username("olduser").storeId(1L).roleId(2L).build();
        SysUserEntity updateUser = SysUserEntity.builder()
                .id(1L).realName("新名字").password(null).build();
        when(sysUserMapper.selectById(1L)).thenReturn(existing);
        doNothing().when(permissionChecker).assertCanManageUser(any());

        sysUserService.update(updateUser);

        // 密码为空时不应加密
        assertNull(updateUser.getPassword());
        verify(sysUserMapper).update(updateUser);
    }

    @Test
    @DisplayName("测试修改用户 - 无权限时抛出403异常")
    void testUpdate_WhenNoPermission_ShouldThrow403() {
        SysUserEntity existing = SysUserEntity.builder()
                .id(1L).username("olduser").storeId(1L).roleId(2L).build();
        SysUserEntity updateUser = SysUserEntity.builder()
                .id(1L).realName("新名字").build();
        when(sysUserMapper.selectById(1L)).thenReturn(existing);
        doThrow(new BusinessException(403, "无权限，只能管理本门店员工"))
                .when(permissionChecker).assertCanManageUser(any());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.update(updateUser));

        assertEquals(403, exception.getCode());
        verify(sysUserMapper, never()).update(any());
    }

    @Test
    @DisplayName("测试修改用户 - 目标员工不存在时抛出1001异常")
    void testUpdate_WhenUserNotExists_ShouldThrow1001() {
        SysUserEntity updateUser = SysUserEntity.builder()
                .id(1L).realName("新名字").build();
        when(sysUserMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.update(updateUser));

        assertEquals(1001, exception.getCode());
        verify(sysUserMapper, never()).update(any());
    }

    @Test
    @DisplayName("测试删除用户 - 调用Mapper的deleteById方法")
    void testRemove_ShouldCallDeleteById() {
        SysUserEntity existing = SysUserEntity.builder()
                .id(1L).username("olduser").storeId(1L).roleId(2L).build();
        // 当前登录人ID为999，不等于目标1L，避免触发"不能删除自己"
        when(permissionChecker.currentUserId()).thenReturn(999L);
        when(sysUserMapper.selectById(1L)).thenReturn(existing);
        doNothing().when(permissionChecker).assertCanManageUser(any());
        when(sysUserMapper.deleteById(1L)).thenReturn(1);

        boolean result = sysUserService.remove(1L);

        assertTrue(result);
        verify(sysUserMapper).deleteById(1L);
    }

    @Test
    @DisplayName("测试删除用户 - 不能删除自己")
    void testRemove_WhenDeleteSelf_ShouldThrow403() {
        when(permissionChecker.currentUserId()).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.remove(1L));

        assertEquals(403, exception.getCode());
        assertEquals("不能删除自己", exception.getMessage());
        verify(sysUserMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试删除用户 - 无权限时抛出403异常")
    void testRemove_WhenNoPermission_ShouldThrow403() {
        SysUserEntity existing = SysUserEntity.builder()
                .id(1L).username("olduser").storeId(1L).roleId(2L).build();
        when(permissionChecker.currentUserId()).thenReturn(999L);
        when(sysUserMapper.selectById(1L)).thenReturn(existing);
        doThrow(new BusinessException(403, "无权限，不能操作同级或上级员工"))
                .when(permissionChecker).assertCanManageUser(any());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.remove(1L));

        assertEquals(403, exception.getCode());
        verify(sysUserMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试登录 - 账号不存在时抛出业务异常")
    void testLogin_WhenUserNotExists_ShouldThrowException() {
        when(sysUserMapper.selectByUsername("nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.login("nonexistent", "123456"));

        assertEquals(1010, exception.getCode());
        assertEquals("账号不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 账号被禁用时抛出业务异常")
    void testLogin_WhenUserDisabled_ShouldThrowException() {
        SysUserEntity disabledUser = SysUserEntity.builder()
                .id(1L).username("disabled").password("hashed").isDeleted(1).build();
        when(sysUserMapper.selectByUsername("disabled")).thenReturn(disabledUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.login("disabled", "123456"));

        assertEquals(1011, exception.getCode());
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 密码错误时抛出业务异常")
    void testLogin_WhenPasswordWrong_ShouldThrowException() {
        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("zhangsan").password(SecureUtil.md5("correctpwd")).isDeleted(0).build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(user);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.login("zhangsan", "wrongpwd"));

        assertEquals(1013, exception.getCode());
        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 账号密码正确时返回JWT令牌")
    void testLogin_WhenCredentialsCorrect_ShouldReturnToken() {
        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("zhangsan").password(SecureUtil.md5("correctpwd")).isDeleted(0).build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L, "zhangsan")).thenReturn("mock-jwt-token");

        String token = sysUserService.login("zhangsan", "correctpwd");

        assertNotNull(token);
        assertEquals("mock-jwt-token", token);
        verify(jwtUtil).generateAccessToken(1L, "zhangsan");
    }
}
