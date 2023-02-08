package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ShopInformationsGetter;
import fr.unice.polytech.cf.model.enumeration.StateRecipe;
import fr.unice.polytech.cf.model.recipe.Dough;
import fr.unice.polytech.cf.model.recipe.Flavor;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.recipe.IngredientCreator;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.recipe.Topping;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.OrderRepository;
import fr.unice.polytech.cf.repositories.RecipeRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import fr.unice.polytech.cf.utils.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
public class ShopShowcaseTest {

    @Autowired
    private ShopInformationsGetter shopInformationsGetter;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    RecipeRepository recipeRepository;


    @Autowired
    OrderRepository orderRepository;


    @Autowired
    CookRepository cookRepository;


    @Autowired
    private OccasionRepository occasionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    Ingredient dough;
    Ingredient flavor;
    Ingredient topping;

    Recipe recipe;

    Shop shop;

    Cook cook;

    Theme theme;
    Occasion occasion ;

    Theme theme2;

    Theme theme3;


    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        orderRepository.deleteAll();
        cookRepository.deleteAll();
        recipeRepository.deleteAll();
        shopRepository.deleteAll();
        shop = new Shop("nice");

        dough = IngredientCreator.createIngredient(1.5f,"dough", Constant.TYPE_DOUGH);
        flavor = IngredientCreator.createIngredient(1.5f,"flavor", Constant.TYPE_FLAVOR);
        topping = IngredientCreator.createIngredient(1.5f,"topping", Constant.TYPE_TOPPING);


        shop.getStock().put(dough,10);
        shop.getStock().put(topping,10);
        shop.getStock().put(flavor,10);
        shop.setOpeningHour(LocalTime.of(0,0));
        shop.setClosingHour(LocalTime.of(23,59));


        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.getIngredients().put(dough,1);
        recipe.getIngredients().put(topping,1);
        recipe.getIngredients().put(flavor,1);
        recipe.setPreparingTime(10);
        recipe.setStateRecipe(StateRecipe.AVAILABLE);
        recipeRepository.save(recipe,recipe.getId());



        theme = new Theme("fleur");
        theme2 = new Theme("etoile");
        theme3 = new Theme("nuit");
        themeRepository.save(theme,theme.getId());
        themeRepository.save(theme2,theme2.getId());
        occasion = new Occasion("anniversaire");
        occasionRepository.save(occasion,occasion.getId());
        shop.getOccasionIds().add(occasion.getId());
        cook = new Cook("marie",shop.getId());
        cook.getThemeIds().add(theme.getId());
        cook.setEndHour(shop.getClosingHour());
        cook.setBeginHour(shop.getOpeningHour());
        cook.getThemeIds().add(theme.getId());

        Cook cook2 = new Cook("claire",shop.getId());
        cook2.getThemeIds().add(theme2.getId());
        cook2.setEndHour(shop.getClosingHour());
        cook2.setBeginHour(shop.getOpeningHour());
        cook2.getThemeIds().add(theme.getId());
        shopRepository.save(shop,shop.getId());
        cookRepository.save(cook,cook.getId());
        cookRepository.save(cook2,cook2.getId());
    }

    @Test
    public void getThemeByShops() throws ResourceNotFoundException {
        List<Theme>  themes = shopInformationsGetter.getThemeByShops(shop);
        assertSame(themes.contains(theme), true);
        assertSame(themes.contains(theme2), true);
        assertSame(themes.size(), 2);
    }

    @Test
    public void getThemeByShops2() throws ResourceNotFoundException {
        cook.getThemeIds().add(theme.getId());
        cook.getThemeIds().add(theme2.getId());
        List<Theme>  themes = shopInformationsGetter.getThemeByShops(shop);
        assertSame(themes.contains(theme), true);
        assertSame(themes.contains(theme2), true);
        assertSame(themes.size(), 2);
    }

    @Test
    public void getThemeByShopsError() throws ResourceNotFoundException {
        cook.getThemeIds().add(theme3.getId());
        Assertions.assertThrows( ResourceNotFoundException.class, () -> {
            shopInformationsGetter.getThemeByShops(shop);
        });
    }

    @Test
    public void getOccasionByShops() throws ResourceNotFoundException {
        List<Occasion>  occasions = shopInformationsGetter.getOccasionByShops(shop);
        assertSame(occasions.contains(occasion), true);
        assertSame(occasions.size(), 1);
    }

    @Test
    public void getIngredientInStockByShops() throws ResourceNotFoundException {
        List<Ingredient>  ingredientList = shopInformationsGetter.getIngredientInStockByShops(shop);
        assertSame(ingredientList.contains(dough), true);
        assertSame(ingredientList.contains(topping), true);
        assertSame(ingredientList.contains(flavor), true);
        assertSame(ingredientList.size(), 3);
    }

    @Test
    public void getRecipeInStockByShops() throws ResourceNotFoundException {
        List<Recipe>  recipes = shopInformationsGetter.getRecipeInStockByShops(shop);
        assertSame(recipes.contains(recipe), true);
        assertSame(recipes.size(), 1);
    }

    @Test
    public void getRecipeInStockByShopsError() throws ResourceNotFoundException {
        recipe.getIngredients().put(dough,30);
        recipeRepository.save(recipe,recipe.getId());
        List<Recipe>  recipes = shopInformationsGetter.getRecipeInStockByShops(shop);
        assertSame(recipes.contains(recipe), false);
        assertSame(recipes.size(), 0);
    }

    @Test
    public void getRecipeInStockByShopsError2() throws ResourceNotFoundException {
        recipe.getIngredients().put(new Dough(1,"dough 2"),1);
        recipeRepository.save(recipe,recipe.getId());
        List<Recipe>  recipes = shopInformationsGetter.getRecipeInStockByShops(shop);
        assertSame(recipes.contains(recipe), false);
        assertSame(recipes.size(), 0);
    }
}
