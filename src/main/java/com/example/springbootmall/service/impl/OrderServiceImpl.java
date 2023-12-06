package com.example.springbootmall.service.impl;

import com.example.springbootmall.dao.OrderDao;
import com.example.springbootmall.dao.ProductDao;
import com.example.springbootmall.dto.BuyItem;
import com.example.springbootmall.dto.CreateOrderRequest;
import com.example.springbootmall.model.OrderItem;
import com.example.springbootmall.model.Product;
import com.example.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;

    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        int totalAmount = 0;
        List<OrderItem> orderItemList=new ArrayList<>();

        //遍歷了 createOrderRequest 中的 buyItemList (List<BuyItem>)
        //BuyItem有productId 和 quantity，價格要去product取得
        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {
            Product product = productDao.getProductId(buyItem.getProductId());
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            //轉換 buyItem 成 orderItem
            OrderItem orderItem=new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);
            orderItemList.add(orderItem);
        }
        //創建訂單
        Integer orderId = orderDao.createOrder(userId,totalAmount);
        //創建明細
        orderDao.createOrderItems(orderId,orderItemList);
        return orderId;
    }
}
