package fr.unice.polytech.cf.model.recipe;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Occasion {

    private UUID id ;

    private  String name;

    public Occasion(String name) {
        this.name = name;
        this.id =UUID.randomUUID();
    }
}
