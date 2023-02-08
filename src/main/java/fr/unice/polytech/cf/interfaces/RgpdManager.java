package fr.unice.polytech.cf.interfaces;

import java.util.UUID;

public interface RgpdManager {

    void deleteClient(UUID clientId);

    void deleteOrder(UUID orderId);
}
