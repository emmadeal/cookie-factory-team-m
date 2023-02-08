package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.client.User;

import java.util.List;

public interface InformationUserGiver {

    List<Order> getHistoricOrder(User user);
}
