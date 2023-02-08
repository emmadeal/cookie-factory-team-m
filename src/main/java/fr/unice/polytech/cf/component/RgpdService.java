package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.interfaces.RgpdManager;
import fr.unice.polytech.cf.repositories.ClientRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RgpdService implements RgpdManager {

    private final ClientRepository clientRepository;

    private final OrderRepository orderRepository;

    // ajouter supp order
    @Override
    public void deleteClient(UUID clientId) {
        clientRepository.deleteById(clientId);
    }

    @Override
    public void deleteOrder(UUID orderId) {
        orderRepository.deleteById(orderId);
    }
}
