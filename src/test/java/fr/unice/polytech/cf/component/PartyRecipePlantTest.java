package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.NegativeQuantityException;
import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.PartyRecipeCreator;
import fr.unice.polytech.cf.interfaces.RecipeFactoryManager;
import fr.unice.polytech.cf.model.client.Client;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.client.Order;
import fr.unice.polytech.cf.model.enumeration.Size;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.PartyRecipe;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PartyRecipePlantTest {

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogController catalogController;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    RecipeFactoryManager recipeFactoryManager;

    @Autowired
    PartyRecipeCreator partyRecipeCreator;


    @Autowired
    private OccasionRepository occasionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ShopRepository shopRepository;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Ingredient dough1;

    Recipe recipe;

    Client claire ;

    Shop shop;

    Theme theme;
    Occasion occasion ;

    HashMap<Ingredient, Integer> ingredients;

    HashMap<Ingredient, Integer> ingredients2;

    HashMap<Ingredient, Integer> ingredients3;

    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        catalogRepository.deleteAll();
        recipeRepository.deleteAll();
        catalogController.registerCatalog();
        shopRepository.deleteAll();

        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);

        dough1 = IngredientCreator.createIngredient(1.5f,"dough1", Constant.TYPE_DOUGH);


        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setName("recipe 0");
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());
        catalogController.registerCatalog();


        claire = new Client();
        Order order = new Order();
        shop = new Shop("nice");
        order.setShop(shop);
        order.setBasket(new HashSet<>());
        order.setClientId(claire.getId());
        order.setLoginClient(false);
        claire.setActualOrder(order);

        theme = new Theme("fleur");
        themeRepository.save(theme,theme.getId());
        occasion = new Occasion("anniversaire");
        occasionRepository.save(occasion,occasion.getId());

        shop.getStock().put(dough,20);
        shop.getStock().put(topping,20);
        shop.getStock().put(flavor,20);
        shop.getStock().put(dough1,20);
        shopRepository.save(shop,shop.getId());

        ingredients = new HashMap<>();
        ingredients.put(dough,2);

        ingredients2 = new HashMap<>();
        ingredients2.put(dough,1);

        ingredients3 = new HashMap<>();
        ingredients3.put(dough1,1);



    }


    @Test
    public void createPersonalizedPartyRecipe() throws Exception {
        Set<Item> basket = partyRecipeCreator.createPersonalizedPartyRecipe(claire, recipe, Size.L ,new HashMap<>(), new HashMap<>(),occasion.getId(),theme.getId());
        assertSame(basket.size(), 1);
        boolean isPresent = basket.stream().anyMatch(item1 ->
                item1.getQuantity()==4
        );
        assertTrue(isPresent);
        isPresent = basket.stream().anyMatch(item1 ->
                item1.getRecipe() instanceof PartyRecipe
        );
        assertTrue(isPresent);

        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),16);

    }

    @Test
    public void createPersonalizedPartyRecipeAddIngredient() throws Exception {
        Set<Item> basket = partyRecipeCreator.createPersonalizedPartyRecipe(claire, recipe, Size.L ,ingredients , new HashMap<>(),occasion.getId(),theme.getId());
        assertSame(basket.size(), 1);
        boolean isPresent = basket.stream().anyMatch(item1 ->
                item1.getQuantity()==4
        );
        assertTrue(isPresent);
        isPresent = basket.stream().anyMatch(item1 ->
                item1.getRecipe() instanceof PartyRecipe
        );
        assertTrue(isPresent);

        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),8);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),16);

    }

    @Test
    public void createPersonalizedPartyRecipeAddNewIngredient() throws Exception {
        Set<Item> basket = partyRecipeCreator.createPersonalizedPartyRecipe(claire, recipe, Size.L ,ingredients3 , new HashMap<>(),occasion.getId(),theme.getId());
        assertSame(basket.size(), 1);
        boolean isPresent = basket.stream().anyMatch(item1 ->
                item1.getQuantity()==4
        );
        assertTrue(isPresent);
        isPresent = basket.stream().anyMatch(item1 ->
                item1.getRecipe() instanceof PartyRecipe
        );
        assertTrue(isPresent);

        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough1),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),16);

    }


    @Test
    public void createPersonalizedPartyRecipeRemoveIngredient() throws Exception {
        Set<Item> basket = partyRecipeCreator.createPersonalizedPartyRecipe(claire, recipe, Size.L  , new HashMap<>(),ingredients2,occasion.getId(),theme.getId());
        assertSame(basket.size(), 1);
        boolean isPresent = basket.stream().anyMatch(item1 ->
                item1.getQuantity()==4
        );
        assertTrue(isPresent);
        isPresent = basket.stream().anyMatch(item1 ->
                item1.getRecipe() instanceof PartyRecipe
        );
        assertTrue(isPresent);

        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(dough),20);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(flavor),16);
        assertEquals(shopRepository.findById(shop.getId()).get().getStock().get(topping),16);

    }


    @Test
    public void createPersonalizedPartyRecipeRemoveIngredientError() throws Exception {
        Assertions.assertThrows( NegativeQuantityException.class, () -> {
            partyRecipeCreator.createPersonalizedPartyRecipe(claire, recipe, Size.L  , new HashMap<>(),ingredients,occasion.getId(),theme.getId());
        });
    }

    @Test
    public void createPersonalizedPartyRecipeRemoveIngredientError2() throws Exception {
        Assertions.assertThrows( NegativeQuantityException.class, () -> {
            partyRecipeCreator.createPersonalizedPartyRecipe(claire, recipe, Size.L  , new HashMap<>(),ingredients3,occasion.getId(),theme.getId());
        });
    }

}
