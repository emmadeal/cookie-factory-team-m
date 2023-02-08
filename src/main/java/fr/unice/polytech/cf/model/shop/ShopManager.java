package fr.unice.polytech.cf.model.shop;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ShopManager {

    private UUID id;

    private String name;

    private UUID shopId;

    public ShopManager(String name, UUID shopId) {
        this.id =UUID.randomUUID();
        this.name = name;
        this.shopId = shopId;
    }
}
