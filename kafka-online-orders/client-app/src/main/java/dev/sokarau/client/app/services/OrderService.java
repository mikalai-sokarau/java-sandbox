package dev.sokarau.client.app.services;

import com.google.gson.Gson;
import dev.sokarau.client.app.repository.OrderRepository;
import dev.sokarau.client.app.model.Order;
import dev.sokarau.common.OrderDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class OrderService {
    private static final String ORDERS_TOPIC_NAME = "orders";
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private Gson gson;

    public OrderService(OrderRepository orderRepository) {
        // clean up a database from previous orders when the service starts
        orderRepository.deleteAll();
    }

    public List<Order> allOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> singleOrder(String correlationId) {
        return orderRepository.findOrderByCorrelationId(correlationId);
    }

    public String createOrder(String name) {
        Order order = new Order(name);
        OrderDTO orderDTO = OrderDTO.builder()
                .correlationId(order.getCorrelationId())
                .name(order.getName())
                .status(order.getStatus())
                .build();

        orderRepository.save(order);

        sendMessageWithDelay(orderDTO, "CREATED", 3000);

        return order.getCorrelationId();
    }

    public Order updateOrderStatus(String correlationId, String status) {
        Optional<Order> optionalOrder = orderRepository.findOrderByCorrelationId(correlationId);

        if(optionalOrder.isEmpty()) {
            return null;
        }

        Order order = optionalOrder.get();

        order.setStatus(status);
        order.updateTimestamp();

        orderRepository.save(order);

        return order;
    }

    private void sendMessageWithDelay(OrderDTO orderDTO, String status, int delay){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                orderDTO.setStatus(status);
                kafkaTemplate.send(ORDERS_TOPIC_NAME, 0, "", gson.toJson(orderDTO));
                logger.info("Client sent a message: " + orderDTO);
            }
        }, delay);
    }
}
