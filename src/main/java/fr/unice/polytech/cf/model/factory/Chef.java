package fr.unice.polytech.cf.model.factory;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Chef {

    private UUID id;

    private String name;

    public Chef(String name) {
        this.id =UUID.randomUUID();
        this.name = name;
    }
}
