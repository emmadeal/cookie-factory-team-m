package fr.unice.polytech.cf.component;


import fr.unice.polytech.cf.interfaces.RecipeFactoryManager;
import fr.unice.polytech.cf.interfaces.SalesController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class SalesManager implements SalesController {

    private final RecipeFactoryManager recipeManager;

    private final RecipeRepository recipeRepository;


    @Override
    @Scheduled(cron = "0 0 1 */1 *")
    public void elaborateNewRecipeMonthly() throws Exception {
        Recipe recipeLeastPurchased = new Recipe();
        recipeLeastPurchased.setNumberOfOrders(-1);
        List<Recipe> recipesAvailable = StreamSupport.stream(recipeRepository.findAll().spliterator(), false)
                .filter(recipe -> StateRecipe.AVAILABLE.equals(recipe.getStateRecipe())).toList();

        int numberOfOrderRecipeLeastPurchased = -1;
        for (Recipe recipe : recipesAvailable){
            if(numberOfOrderRecipeLeastPurchased==-1 || recipe.getNumberOfOrders()< numberOfOrderRecipeLeastPurchased ){
                recipeLeastPurchased = recipe;
                numberOfOrderRecipeLeastPurchased = recipe.getNumberOfOrders();
            }
        }
        recipeManager.removeRecipe(recipeLeastPurchased.getId());
        recipeManager.createRecipe("Recipe of"+  LocalDateTime.now().getYear() +" "+ LocalDateTime.now().getMonthValue());
    }


    @Override
    public void updatesSalesOfRecipe(Set<Item> basket) {
        for (Item item : basket){
            Recipe recipe =item.getRecipe();
            recipe.setNumberOfOrders(recipe.getNumberOfOrders()+item.getQuantity());
            recipeRepository.save(recipe,recipe.getId());
        }
    }
}
