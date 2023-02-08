package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.interfaces.OrderProcessing;
import fr.unice.polytech.cf.interfaces.Payment;
import fr.unice.polytech.cf.interfaces.SalesController;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class PaiementService implements Payment  {


    private final OrderRepository orderRepository;

    private final BankAPI bank;

    private final OrderProcessing kitchen;

    private final SalesController salesController;



    @Override
    public Order payOrder(Client client,Order order) throws Exception {
        double price = order.calculatePrice();
        // reduction
        if(order.isLoginClient() && hasReduction((User)client)){
            price = (float) (price*0.9);
        }
        boolean status = false;
        status = bank.pay(client, price);
        if (!status) {
            throw new Exception("no paid");
        }
        order.setState(StateOrder.PAID);
        order.setPrice(price);
        order.setClientId(client.getId());
        orderRepository.save(order,order.getId());
        salesController.updatesSalesOfRecipe(order.getBasket());
        kitchen.process(order);
        return order;
    }

    public boolean hasReduction(User user)  {
        if(user.isMembership()){
            int numberOfRecipe = 0;
            List<Order> passedOrders = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                    .filter(order -> user.getId().equals(order.getClientId())).toList();
            for(Order order : passedOrders){
                numberOfRecipe+=order.getNumberCookies();
            }
            return ( numberOfRecipe != 0 && numberOfRecipe % 30 == 0 );
        }
        return false;
    }


}
