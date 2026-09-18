package com.iwe3.sec.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.PrinterEntity;
import com.iwe3.sec.mapper.OrderDetailMapper;
import com.iwe3.sec.mapper.PrinterMapper;
import com.iwe3.sec.service.IPrintService;

import java.util.Date;
import java.util.List;

/**
 * 打印服务实现类
 * 工作时会按门店找到对应的打印机，把订单内容发过去打印
 */
@Service
public class PrintServiceImpl implements IPrintService {

    private static final Logger log = LoggerFactory.getLogger(PrintServiceImpl.class);

    private final PrinterMapper printerMapper;
    private final OrderDetailMapper orderDetailMapper;

    public PrintServiceImpl(PrinterMapper printerMapper,
                            OrderDetailMapper orderDetailMapper) {
        this.printerMapper = printerMapper;
        this.orderDetailMapper = orderDetailMapper;
    }

    @Override
    public void printKitchenTicket(OrdersEntity order) {
        // 查找门店里已启用的后厨打印机
        List<PrinterEntity> printers = printerMapper.selectByStoreAndType(order.getStoreId(), "KITCHEN");
        if (printers.isEmpty()) {
            log.info("门店 {} 未配置后厨打印机，跳过打印", order.getStoreId());
            return;
        }

        // 组装后厨制作单内容
        String content = buildKitchenContent(order);
        for (PrinterEntity printer : printers) {
            sendToPrinter(printer, content);
        }
    }

    @Override
    public void printReceipt(OrdersEntity order) {
        // 查找门店里已启用的前台小票打印机
        List<PrinterEntity> printers = printerMapper.selectByStoreAndType(order.getStoreId(), "RECEIPT");
        if (printers.isEmpty()) {
            log.info("门店 {} 未配置前台小票打印机，跳过打印", order.getStoreId());
            return;
        }

        // 组装小票内容
        String content = buildReceiptContent(order);
        for (PrinterEntity printer : printers) {
            sendToPrinter(printer, content);
        }
    }

    @Override
    public void sendToPrinter(PrinterEntity printer, String content) {
        // 打印机编号 printer_no 存储的是打印机的 IP 地址
        // 通过 HTTP 协议发送打印指令（常见的热敏打印机接口）
        String printerUrl = "http://" + printer.getPrinterNo() + "/print";
        try {
            String result = HttpRequest.post(printerUrl)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.createObj()
                            .set("content", content)
                            .set("printerName", printer.getPrinterName())
                            .toString())
                    .timeout(5000)
                    .execute()
                    .body();
            log.info("打印指令已发送: printer={}, result={}", printer.getPrinterName(), result);
        } catch (Exception e) {
            log.error("打印失败: printer={}, error={}", printer.getPrinterName(), e.getMessage());
        }
    }

    /**
     * 组装后厨制作单内容
     */
    private String buildKitchenContent(OrdersEntity order) {
        List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(order.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("========== 后厨制作单 ==========\n");
        sb.append("订单号：").append(order.getOrderNo()).append("\n");
        sb.append("下单时间：").append(DateUtil.format(order.getOrderTime(), "HH:mm:ss")).append("\n");
        sb.append("桌号/方式：").append(order.getTakeType() == 1 ? "堂食" : "外卖").append("\n");
        if (order.getRemark() != null && !order.getRemark().isEmpty()) {
            sb.append("备注：").append(order.getRemark()).append("\n");
        }
        sb.append("--------------------------------\n");

        for (OrderDetailEntity detail : details) {
            sb.append(detail.getDishName());
            if (detail.getSpecName() != null) {
                sb.append("(").append(detail.getSpecName()).append(")");
            }
            sb.append(" x").append(detail.getQuantity());
            if (detail.getRemark() != null && !detail.getRemark().isEmpty()) {
                sb.append(" [").append(detail.getRemark()).append("]");
            }
            sb.append("\n");
        }

        sb.append("================================\n");
        sb.append("打印时间：").append(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).append("\n");
        return sb.toString();
    }

    /**
     * 组装前台小票内容
     */
    private String buildReceiptContent(OrdersEntity order) {
        List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(order.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("========== 消费小票 ==========\n");
        sb.append("订单号：").append(order.getOrderNo()).append("\n");
        sb.append("下单时间：").append(DateUtil.format(order.getOrderTime(), "yyyy-MM-dd HH:mm:ss")).append("\n");
        sb.append("--------------------------------\n");

        for (OrderDetailEntity detail : details) {
            sb.append(detail.getDishName());
            if (detail.getSpecName() != null) {
                sb.append("(").append(detail.getSpecName()).append(")");
            }
            sb.append(" x").append(detail.getQuantity());
            sb.append("  ").append(detail.getDishPrice().multiply(
                    java.math.BigDecimal.valueOf(detail.getQuantity()))).append("元\n");
        }

        sb.append("--------------------------------\n");
        sb.append("菜品合计：").append(order.getDishAmount()).append("元\n");

        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            sb.append("优惠：-").append(order.getDiscountAmount()).append("元\n");
        }
        if (order.getCouponAmount() != null && order.getCouponAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            sb.append("优惠券：-").append(order.getCouponAmount()).append("元\n");
        }
        if (order.getDeliveryFee() != null && order.getDeliveryFee().compareTo(java.math.BigDecimal.ZERO) > 0) {
            sb.append("配送费：").append(order.getDeliveryFee()).append("元\n");
        }

        sb.append("实付金额：").append(order.getActualAmount()).append("元\n");
        sb.append("================================\n");
        sb.append("打印时间：").append(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).append("\n");
        return sb.toString();
    }
}
