package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CancelationManager;
import fr.unice.polytech.cf.interfaces.NotificationSender;
import fr.unice.polytech.cf.interfaces.ObsoleteManager;
import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.interfaces.StockController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class ManageOrderService implements ObsoleteManager, CancelationManager {

    private final OrderRepository orderRepository;

    private final OrderProcessing orderProcessing;

    private final StockController stockController;

    private final NotificationSender notificationSender;

    @Override
    public void cancelOrder(Order order) throws ResourceNotFoundException {
        order.setState(StateOrder.CANCEL);
        order.setCancelingTime(LocalDateTime.now());
        orderRepository.save(order,order.getId());
        for(Item item : order.getBasket()){
            item.setQuantity(-1 * item.getQuantity());
            stockController.updateStock(order.getShop(),item);
        }
        orderProcessing.stopOrderProcessing(order);
    }

    @Override
    public boolean userCanOrder(User user){
        List<Order> canceledOrders = getOrdersCanceledByClientId(user.getId());
        int size = canceledOrders.size();
        if(size>1){
            Order lastOrder = canceledOrders.get(size - 1);
            if(ChronoUnit.MINUTES.between(LocalDateTime.now(), lastOrder.getCancelingTime())>=10){
                return true;
            }
            Order secondLastOrder = canceledOrders.get(size - 2);
            return ChronoUnit.MINUTES.between(secondLastOrder.getCancelingTime(), lastOrder.getCancelingTime()) > 8;
        }else{
            return true;
        }
    }

    @Override
    public long timeBeforeUserCanCancelOrder(User user) {
        List<Order> canceledOrders = getOrdersCanceledByClientId(user.getId());
        int size = canceledOrders.size();
        return ChronoUnit.MINUTES.between(canceledOrders.get(size - 1).getCancelingTime(), canceledOrders.get(size - 2).getCancelingTime());
    }

    @Override
    public void obsoleteOrder(Order order) {
        order.setState(StateOrder.OBSOLETE);
        orderRepository.save(order,order.getId());
    }



    @Override
    @Scheduled(cron = "0 */1 * * * *")
    public void checkOrderSate() throws ResourceNotFoundException {
        List<Order> ordersReady = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.READY.equals(order.getState()) && order.getReadyTime()!=null).toList();
        for(Order order : ordersReady){
            // si commande obsolete
            if(ChronoUnit.HOURS.between(order.getReadyTime(),LocalDateTime.now())>=2){
                obsoleteOrder(order);
                continue;
            }
            if(order.isLoginClient()){
                if(ChronoUnit.MINUTES.between(order.getReadyTime(),LocalDateTime.now())>=5 && ChronoUnit.MINUTES.between(order.getReadyTime(),LocalDateTime.now())<=7 ){
                    notificationSender.notifyUser(order,5);
                }
                else if(ChronoUnit.MINUTES.between(order.getReadyTime(),LocalDateTime.now())>=60 && ChronoUnit.MINUTES.between(order.getReadyTime(),LocalDateTime.now())<=62){
                    notificationSender.notifyUser(order,60);
                }
            }
        }
    }


    public List<Order> getOrdersCanceledByClientId(UUID clientId) {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> clientId.equals(order.getClientId()) && StateOrder.CANCEL.equals(order.getState()) && order.getCancelingTime()!=null ).collect(Collectors.toList());
    }

}
