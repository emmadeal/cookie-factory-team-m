package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ShopSubscriberController;
import fr.unice.polytech.cf.interfaces.SurpriseBasketController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.SurpriseBasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class SurpriseBasketService implements SurpriseBasketController  {

    private final SurpriseBasketRepository surpriseBasketRepository;


    private final OrderRepository orderRepository;


    private final ShopSubscriberController shopSubscriberController;

    private final TooGoodToGoAPI tooGoodToGoAPI;


    @Override
    @Scheduled(cron = "0 0 */3 * * *")
    public void createSurpriseBasket() {
        List<Order> obsoleteOrders = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> StateOrder.OBSOLETE.equals(order.getState())).toList();

        obsoleteOrders.forEach(order -> {
            SurpriseBasket surpriseBasket = convertToSurpriseBasket(order);
            surpriseBasket.setState(StateOrder.READY);
            UUID toGoodToGoId = tooGoodToGoAPI.addSurpriseBasket(surpriseBasket);
            surpriseBasket.setTooGoodToGoId(toGoodToGoId);
            surpriseBasketRepository.save(surpriseBasket,surpriseBasket.getId());
            try {
                shopSubscriberController.notifyObservers(surpriseBasket,order.getShop());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            order.setState(StateOrder.TOOGOODTOGO);
            orderRepository.save(order,order.getId());
        });
    }




    public SurpriseBasket convertToSurpriseBasket(Order order) {
        StringBuilder description = new StringBuilder("Panier remplis de : ");
        for (Item item : order.getBasket()) {
            description.append(String.format("%d cookie nommé %s ", item.getQuantity(),item.getRecipe().getName()));
        }
        return new SurpriseBasket(order.getBasket(), order.calculatePrice() / 2, order.getShop(), description.toString());
    }

    @Override
    public boolean reserveSurpriseBasket(SurpriseBasket surpriseBasket) throws Exception {
        UUID surpriseBasketId = surpriseBasket.getId();
        SurpriseBasket surpriseBasketWanted =surpriseBasketRepository.findById(surpriseBasketId).orElseThrow(
                ()-> new ResourceNotFoundException(String.format("No surprise basket found with given id %s",surpriseBasketId.toString()))
        );
        if(surpriseBasketWanted.isReserve()){
            return false;
        }
        surpriseBasketWanted.setReserve(true);
        surpriseBasketWanted.setTooGoodToGoId(surpriseBasket.getTooGoodToGoId());
        surpriseBasketRepository.save(surpriseBasketWanted,surpriseBasketWanted.getId());
        return true;
    }

    @Override
    public void giveSurpriseBasket(UUID surpriseBasketTooGoodToGoId) throws ResourceNotFoundException {
        double bill = tooGoodToGoAPI.paySurpriseBasket(surpriseBasketTooGoodToGoId);
        SurpriseBasket surpriseBasket =StreamSupport.stream(surpriseBasketRepository.findAll().spliterator(), false)
                .filter(user -> surpriseBasketTooGoodToGoId.equals(user.getTooGoodToGoId())).findAny().orElseThrow(
                        ()-> new ResourceNotFoundException(String.format("No surprise basket found with given tooGoodToGoId %s",surpriseBasketTooGoodToGoId.toString()))
                );
        surpriseBasket.setState(StateOrder.TAKEN);
        surpriseBasketRepository.save(surpriseBasket,surpriseBasket.getId());
    }
}
