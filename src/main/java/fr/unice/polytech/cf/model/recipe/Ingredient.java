package fr.unice.polytech.cf.model.recipe;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dough.class, name = "Dough"),
        @JsonSubTypes.Type(value = Flavor.class, name = "Flavor"),
        @JsonSubTypes.Type(value = Topping.class, name = "Topping")
})
@Getter
@Setter
public abstract class Ingredient {

    private UUID id;

    private float price;

    private String name;

    private String type;


    public Ingredient( float price, String name) {
        this.id = UUID.randomUUID();
        this.price = price;
        this.name = name;
    }

    public boolean checkLimit(Recipe recipe, int i, int nb, int limit) {
        if (nb < limit) {
            recipe.addToCheck(i);
            return true;
        } else return false;
    }

    public abstract boolean checkIngredients(Recipe recipe);
}
