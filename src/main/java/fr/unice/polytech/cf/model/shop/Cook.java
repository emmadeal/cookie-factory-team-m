package fr.unice.polytech.cf.model.shop;


import fr.unice.polytech.cf.model.client.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Cook {

    private UUID id;

    private UUID shopId;

    private String name;

     private LocalTime beginHour;

    private LocalTime endHour;

    private List<UUID> themeIds = new ArrayList<>() ;

    private List<Order> ordersInProgress = new ArrayList<>();

    public Cook(String name, UUID shopId) {
        this.id = UUID.randomUUID();
        this.shopId = shopId;
        this.name = name;
    }
}
