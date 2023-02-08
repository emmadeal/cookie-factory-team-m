package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;

import java.util.Set;

public interface BasketModifier {

    Set<Item> addInBasket(Client client, Item item) throws Exception;
    Set<Item> removeInBasket(Client client, Item item) throws Exception;
}
