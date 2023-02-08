package fr.unice.polytech.cf.model.client;

import fr.unice.polytech.cf.model.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Item {

    private Recipe recipe;

    private int quantity;
}
