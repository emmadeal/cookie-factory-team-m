package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.EmptyBasketException;
import fr.unice.polytech.cf.exception.NegativeQuantityException;
import fr.unice.polytech.cf.exception.NoPickUpHourtException;
import fr.unice.polytech.cf.exception.NoShopException;
import fr.unice.polytech.cf.interfaces.BasketModifier;
import fr.unice.polytech.cf.interfaces.BasketProcessor;
import fr.unice.polytech.cf.interfaces.Payment;
import fr.unice.polytech.cf.interfaces.StockController;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.shop.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;


@Component
@Slf4j
@RequiredArgsConstructor
public class CartHandler implements BasketModifier, BasketProcessor {


    private final StockController stockController;

    private final Payment payment;



    @Override
    public Set<Item> addInBasket(Client client, Item itemSelect) throws Exception {
        Order order = client.getActualOrder();
        Shop shop = order.getShop();
        Set<Item> basket = order.getBasket();
        if(shop!=null) {
            if(stockController.verifyStock(shop,itemSelect)) {
                int newQuantity = itemSelect.getQuantity();
                Optional<Item> existingItem = basket.stream().filter(item -> item.getRecipe().equals(itemSelect.getRecipe())).findFirst();
                if (existingItem.isPresent()) {
                    newQuantity += existingItem.get().getQuantity();
                }
                stockController.updateStock(shop,itemSelect);
                existingItem.ifPresent(basket::remove);
                basket.add(new Item(itemSelect.getRecipe(), newQuantity));
            }
            return basket;
        }
        throw new NoShopException("Le client n'a pas spécifié de magasin pour éxécuter sa commande");
    }


    @Override
    public Set<Item> removeInBasket(Client client , Item itemSelect) throws Exception {
        Order order = client.getActualOrder();
        Shop shop = order.getShop();
        if(shop!=null){
            Set<Item> basket = order.getBasket();
            int newQuantity = -itemSelect.getQuantity();
            Optional<Item> existingItem = basket.stream().filter(item -> item.getRecipe().equals(itemSelect.getRecipe())).findFirst();
            if (existingItem.isPresent()) {
                newQuantity += existingItem.get().getQuantity();
                if (newQuantity < 0) {
                    throw new NegativeQuantityException("quantité négative");
                } else {
                    existingItem.ifPresent(basket::remove);
                    if (newQuantity > 0) {
                        basket.add(new Item(itemSelect.getRecipe(), newQuantity));
                    }
                    Item item = new Item(itemSelect.getRecipe(),-itemSelect.getQuantity());
                    stockController.updateStock(shop,item);
                }
            }
            return basket;
        }
        throw new NoShopException("Le client n'a pas spécifié de magasin pour éxécuter sa commande");
    }


    @Override
    public Order validate(Client client) throws Exception {
        Order actualOrder = client.getActualOrder();
        checkOrderIsValid(actualOrder);
        return payment.payOrder(client,actualOrder);
    }

    private void checkOrderIsValid(Order order) throws Exception {
        if (order.getBasket().isEmpty())
            throw new EmptyBasketException("basket vide");
        if(order.getShop()==null ){
            throw new NoShopException("shop vide");
        }if(order.getPickUpHour()==null){
            throw  new NoPickUpHourtException("pickup hour vide");
        }
    }


}
