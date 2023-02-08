package fr.unice.polytech.cf.model.client;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
public class Client {

    public Client() {
        this.id = UUID.randomUUID();
    }

    private UUID id;

    private Order actualOrder = new Order();

    public Client(Order actualOrder) {
        this.actualOrder = actualOrder;
        this.id = UUID.randomUUID();
    }
}
