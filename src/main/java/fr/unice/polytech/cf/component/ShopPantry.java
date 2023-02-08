package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.StockController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShopPantry implements StockController {

    private final ShopRepository shopRepository;


    @Override
    public boolean verifyStock(Shop shop, Item item)  {
        Recipe recipe = item.getRecipe();
        for (Ingredient ingredient : recipe.getIngredients().keySet()) {
            HashMap<Ingredient, Integer> stock =shop.getStock();
            if (!stock.containsKey(ingredient)) return false;
            if (stock.get(ingredient) < recipe.getIngredients().get(ingredient) * item.getQuantity() ) return false;
        }
        return true;
    }

    @Override
    public void updateStock(Shop shop, Item item) throws ResourceNotFoundException {
        Recipe recipe = item.getRecipe();
        for (Ingredient ingredient : recipe.getIngredients().keySet()) {
            int quantityOfIngredient = recipe.getIngredients().get(ingredient);
            if(item.getQuantity()>0)
                for (int i = 0; i < quantityOfIngredient*item.getQuantity(); i++){
                    removeFromStock(shop.getId(),ingredient);
                }
            else
                for (int i = 0; i < (quantityOfIngredient*item.getQuantity())*-1; i++) {
                    addToStock(shop.getId(),ingredient);
                }
        }
    }

    @Override
    public void addToStock(UUID shopId, Ingredient ingredient) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        HashMap<Ingredient, Integer> stock =shop.getStock();
        if (stock.containsKey(ingredient))
            stock.put(ingredient, stock.get(ingredient) + 1);
        else
            stock.put(ingredient, 1);
        shopRepository.save(shop,shopId);
    }


    @Override
    public void removeFromStock(UUID shopId,Ingredient ingredient) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        HashMap<Ingredient, Integer> stock = shop.getStock();
        if(!stock.containsKey(ingredient)){
            throw new ResourceNotFoundException("Le client essaye de commander un cookie contenant un ingrédient plus disponible");
        }
        else if (stock.get(ingredient) == 1)
            stock.remove(ingredient);
        else {
            stock.put(ingredient, stock.get(ingredient) - 1);
        }
        shopRepository.save(shop, shopId);
    }

    public Shop getShopById(UUID shopId) throws ResourceNotFoundException {
        return shopRepository.findById(shopId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No shop found with given id %s", shopId.toString()))
        );
    }
}
