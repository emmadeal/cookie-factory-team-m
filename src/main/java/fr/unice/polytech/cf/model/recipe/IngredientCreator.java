package fr.unice.polytech.cf.model.recipe;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.utils.Constant;

public class IngredientCreator {



    public static Ingredient createIngredient( float price, String name,String type) throws ResourceNotFoundException {
        Ingredient ingredient = switch (type){
            case Constant.TYPE_TOPPING -> new Topping(price,name);
            case Constant.TYPE_DOUGH -> new Dough(price,name);
            case Constant.TYPE_FLAVOR -> new Flavor(price,name);
            default -> throw new ResourceNotFoundException( "Le type d'ingrédient que vous voulez créer n'existe pas" );
        };
        return ingredient;
    }
}
