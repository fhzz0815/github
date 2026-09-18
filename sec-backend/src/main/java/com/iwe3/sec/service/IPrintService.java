package com.iwe3.sec.service;

import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.entity.PrinterEntity;

/**
 * 打印服务接口
 * 负责将订单内容发送到门店对应的打印机
 */
public interface IPrintService {

    /**
     * 打印后厨制作单
     * 订单创建/支付成功后触发，发送到 KITCHEN 类型的打印机
     */
    void printKitchenTicket(OrdersEntity order);

    /**
     * 打印前台小票
     * 支付成功后触发，发送到 RECEIPT 类型的打印机
     */
    void printReceipt(OrdersEntity order);

    /**
     * 发送打印指令到指定打印机
     *
     * @param printer 打印机配置
     * @param content 打印内容（纯文本格式，打印机按行打印）
     */
    void sendToPrinter(PrinterEntity printer, String content);
}
