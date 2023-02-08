package fr.unice.polytech.cf.model.recipe;

import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    private UUID id;

    private HashMap<Ingredient, Integer> ingredients = new HashMap<>();

    private String name;

    private double price =0;

    private boolean mixed=false;

    private boolean wellDone=false;

    private long cookingTime;

    private long preparingTime;

    private int[] checkNb = new int[3];

    private int numberOfOrders =0;

    private StateRecipe stateRecipe;

    public Recipe(UUID id, String name, StateRecipe stateRecipe) {
        this.id = id;
        this.name = name;
        this.stateRecipe = stateRecipe;
    }

    public Recipe(HashMap<Ingredient, Integer> ingredients, String name) {
        this.ingredients = ingredients;
        this.name = name;
    }

    public double getPrice(){
        price =0;
        for (Ingredient ingredient : ingredients.keySet()) {
            if(this instanceof PartyRecipe){
                price += (ingredient.getPrice() * ingredients.get(ingredient))*1.25;
            }else{
                price += ingredient.getPrice() * ingredients.get(ingredient);
            }
        }
        return price;
    }

    public boolean isValid() {
        return (checkNb[0] == 1 && checkNb[1] <= 1 && checkNb[2] <= 3);
    }

    public void addToCheck(int i) {
        this.checkNb[i]++;
    }

    public int getNbD() {
        return checkNb[0];
    }

    public int getNbF() {
        return checkNb[1];
    }

    public int getNbT() {
        return checkNb[2];
    }

}
