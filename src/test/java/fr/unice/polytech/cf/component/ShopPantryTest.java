package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.StockController;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.utils.Constant;
import io.cucumber.java.sl.In;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ShopPantryTest  {


    @Autowired
    private StockController stockController;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;


    @Autowired
    CookRepository cookRepository;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();

        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);

        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);

        recipeRepository.save(recipe,recipe.getId());
        shop = new Shop("nice");
        shop.getStock().put(dough,20);
        shop.getStock().put(topping,20);
        shop.getStock().put(flavor,20);
        shopRepository.save(shop,shop.getId());

        cook = new Cook("marie",shop.getId());
        cookRepository.save(cook,cook.getId());
    }

    @Test
    public void verifyStock() throws ResourceNotFoundException {
        assertTrue( stockController.verifyStock(shop,new Item(recipe,1)));
        assertTrue(stockController.verifyStock(shop, new Item(recipe, 20)));
    }

    @Test
    public void verifyStockFalse() throws ResourceNotFoundException {
        assertFalse( stockController.verifyStock(shop,new Item(recipe,25)));
    }

    @Test
    public void verifyStockFalseOtherIngredient() throws ResourceNotFoundException {
        Flavor flavor1 = new Flavor(1,"flavor1");
        recipe.getIngredients().put(flavor1,3);
        assertFalse( stockController.verifyStock(shop,new Item(recipe,1)));
    }


    @Test
    public void UpdateStockRemove() throws ResourceNotFoundException {
        stockController.updateStock(shop,new Item(recipe,1));
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),19);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),19);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),19);

    }

    @Test
    public void UpdateStockAdd() throws ResourceNotFoundException {
        stockController.updateStock(shop,new Item(recipe,-1));
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),21);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),21);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),21);

    }

    @Test
    public void UpdateStockAddOtherIngredient() throws ResourceNotFoundException {
        Flavor flavor1 = new Flavor(1,"flavor1");
        recipe.getIngredients().put(flavor1,3);
        stockController.updateStock(shop,new Item(recipe,-1));
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),21);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),21);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),21);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor1),3);
    }

    @Test
    public void UpdateStockRemoveOtherIngredientError() throws ResourceNotFoundException {
        Flavor flavor1 = new Flavor(1,"flavor1");
        recipe.getIngredients().put(flavor1,3);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            stockController.updateStock(shop,new Item(recipe,1));
        });

    }


}
