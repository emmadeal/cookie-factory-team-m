package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.BadStateException;
import fr.unice.polytech.cf.interfaces.CheckRetrieveOrder;
import fr.unice.polytech.cf.interfaces.RgpdManager;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CounterCollect implements CheckRetrieveOrder {
    private final OrderRepository orderRepository;

    private final RgpdManager rgpdManager;

    @Override
    public void giveOrder(UUID orderId) throws BadStateException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order =optionalOrder.get();
            if(order.getState()== StateOrder.READY){
                order.setState(StateOrder.TAKEN);
                orderRepository.save(order,order.getId());
                //une fois la commande recup si c'est un client on supprime de la base de données
                if(!order.isLoginClient()){
                   // rgpdManager.deleteOrder(order.getId());
                    rgpdManager.deleteClient(order.getClientId());
                }
                return;
            }
        }
        throw  new BadStateException("Cet id ne correspond a une commande recuperable");
    }
}
