package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class Kitchen implements OrderProcessing {

    private final OrderRepository orderRepository;

    private final CookRepository cookRepository;


    @Override
    public void process(Order order) throws Exception {
        Cook cook = getCookById(order.getCookId());
        cook.getOrdersInProgress().add(order);
        cookRepository.save(cook,cook.getId());
    }

    @Override
    @Scheduled(cron = "0 */1 * * * *")
    public void readyOrders() throws ResourceNotFoundException {
        List<Order> ordersPaid = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.PAID.equals(order.getState())).toList();
        for(Order order : ordersPaid){
            if(order.getPickUpHour().isBefore(LocalDateTime.now())){
                order.setState(StateOrder.READY);
                order.setReadyTime(LocalDateTime.now());
                orderRepository.save(order,order.getId());
                Cook cook =getCookById(order.getCookId());
                cook.getOrdersInProgress().remove(order);
                cookRepository.save(cook,cook.getId());
            }
        }
    }

    @Override
    public void stopOrderProcessing(Order order) throws ResourceNotFoundException {
        Cook cook = getCookById(order.getCookId());
        cook.getOrdersInProgress().remove(order);
        cookRepository.save(cook,cook.getId());
    }

    public Cook getCookById(UUID cookId) throws ResourceNotFoundException {
        return cookRepository.findById(cookId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No cook found with given id %s", cookId.toString()))
        );
    }
    
    
}
