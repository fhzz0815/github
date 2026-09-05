package com.iwe3.sec.integration;

import com.iwe3.sec.SecApplication;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.service.IAuthService;
import com.iwe3.sec.service.IDiningTableService;
import com.iwe3.sec.service.IFeedbackService;
import com.iwe3.sec.service.IStoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试：真实 MySQL 数据库的完整增删改查链路
 * 整个类加事务并自动回滚，测试产生的数据不会落库，可重复执行。
 */
@SpringBootTest(classes = SecApplication.class)
@Transactional
@Rollback
class CrudIntegrationTest {

    @Autowired
    private IStoreService storeService;
    @Autowired
    private IDiningTableService diningTableService;
    @Autowired
    private IFeedbackService feedbackService;
    @Autowired
    private IAuthService authService;

    private final String marker = "IT" + System.currentTimeMillis();

    @Test
    @DisplayName("门店：新增 -> 关键字搜索 -> 详情 -> 修改 -> 删除 完整链路")
    void store_fullCrudFlow() {
        // 1. 新增
        StoreEntity store = StoreEntity.builder()
                .storeNo(marker + "sto")
                .storeName(marker + "测试门店")
                .build();
        assertTrue(storeService.add(store), "新增门店应返回成功");
        assertNotNull(store.getId(), "新增后主键应回填");

        // 2. 关键字模糊搜索能查到
        StoreEntity query = new StoreEntity();
        query.setSearchKeyword(marker);
        PageResult<StoreEntity> page = storeService.list(query, 1, 10);
        assertTrue(page.getList().stream().anyMatch(s -> (marker + "sto").equals(s.getStoreNo())),
                "按门店编号关键字应能搜到新数据");

        // 3. 详情
        StoreEntity found = storeService.getById(store.getId());
        assertNotNull(found);
        assertEquals(marker + "测试门店", found.getStoreName());

        // 4. 修改
        found.setStoreName(marker + "改名后");
        assertTrue(storeService.update(found));
        assertEquals(marker + "改名后", storeService.getById(store.getId()).getStoreName());

        // 5. 删除（门店是逻辑删除）
        assertTrue(storeService.remove(store.getId()));

        // 6. 逻辑删除后：详情查不到（selectById 带 is_deleted=0），列表也搜不到
        assertNull(storeService.getById(store.getId()), "逻辑删除后详情应查不到");
        PageResult<StoreEntity> after = storeService.list(query, 1, 10);
        assertTrue(after.getList().stream().noneMatch(s -> (marker + "sto").equals(s.getStoreNo())),
                "删除后列表不应再出现该数据");
    }

    @Test
    @DisplayName("台桌：外键关联新增 -> 搜索 -> 删除")
    void diningTable_crudFlow() {
        DiningTableEntity table = DiningTableEntity.builder()
                .storeId(1L)
                .tableTypeId(1L)
                .tableNo(marker + "tab")
                .tableName("IT测试桌")
                .status(1)
                .build();
        assertTrue(diningTableService.add(table));

        DiningTableEntity query = new DiningTableEntity();
        query.setSearchKeyword(marker);
        PageResult<DiningTableEntity> page = diningTableService.list(query, 1, 10);
        assertEquals(1, page.getList().size(), "按台桌编号应精确搜到 1 条");

        assertTrue(diningTableService.remove(table.getId()));
        assertEquals(0, diningTableService.list(query, 1, 10).getList().size(), "删除后搜不到");
    }

    @Test
    @DisplayName("意见反馈：新增后按内容关键字可搜索")
    void feedback_crudFlow() {
        FeedbackEntity feedback = FeedbackEntity.builder()
                .content(marker + " 这是一条集成测试反馈")
                .contactPhone("13900000000")
                .status(0)
                .build();
        assertTrue(feedbackService.add(feedback));

        FeedbackEntity query = new FeedbackEntity();
        query.setSearchKeyword(marker);
        PageResult<FeedbackEntity> page = feedbackService.list(query, 1, 10);
        assertTrue(page.getList().size() >= 1, "应能按内容关键字搜到反馈");

        assertTrue(feedbackService.remove(feedback.getId()));
    }

    @Test
    @DisplayName("认证：正确账号登录返回令牌；错误密码/不存在账号抛出业务异常")
    void auth_loginFlow() {
        String token = authService.login("13800000001", "123456");
        assertNotNull(token, "正确账号密码应返回 JWT 令牌");
        assertTrue(token.length() > 20, "令牌应为完整 JWT");

        BusinessException wrongPwd = assertThrows(BusinessException.class,
                () -> authService.login("13800000001", "wrong-password"));
        assertEquals(1004, wrongPwd.getCode(), "错误密码应返回 1004");

        BusinessException noUser = assertThrows(BusinessException.class,
                () -> authService.login("not_exist_user_999", "123456"));
        assertEquals(1002, noUser.getCode(), "账号不存在应返回 1002");
    }

    @Test
    @DisplayName("搜索：创建时间范围过滤生效")
    void search_timeRangeFilter() {
        // 宽区间：应包含全部存量数据
        StoreEntity range = new StoreEntity();
        range.setSearchBeginTime("2020-01-01");
        range.setSearchEndTime("2030-12-31");
        PageResult<StoreEntity> wide = storeService.list(range, 1, 100);
        assertTrue(wide.getTotal() >= 3, "宽区间应包含存量门店");

        // 早于系统上线的区间：应为 0 条
        StoreEntity early = new StoreEntity();
        early.setSearchBeginTime("2000-01-01");
        early.setSearchEndTime("2000-12-31");
        assertEquals(0L, storeService.list(early, 1, 100).getTotal(), "2000年区间不应有数据");
    }
}
