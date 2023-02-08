package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.interfaces.ShopInformationsModifier;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ShopInformationServiceTest {

    @Autowired
    ShopInformationsModifier shopInformationsModifier;

    @Autowired
    private  ShopRepository shopRepository;

    @Autowired
    private  OccasionRepository occasionRepository;

    @Autowired
    private  ThemeRepository themeRepository;

    @Autowired
    private  CookRepository cookRepository;



    Shop shop;

    Cook cook;


    @BeforeEach
    void setUp()  {
        occasionRepository.deleteAll();
        themeRepository.deleteAll();
        shopRepository.deleteAll();
        cookRepository.deleteAll();

        shop = new Shop("nice");
         shopRepository.save(shop,shop.getId());

        cook = new Cook("marie",shop.getId());
        cookRepository.save(cook,cook.getId());
    }

    @Test
    public void fixTax() throws Exception {
        shopInformationsModifier.fixTax(shop.getId(),10);
        boolean isPresent = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .anyMatch(shop1 -> 10==shop1.getTax());
        assertTrue(isPresent);
    }

    @Test
    public void addOccasion() throws Exception {
        shopInformationsModifier.addOccasion(shop.getId(),"anniversaire");
        Shop shopRepo = shopRepository.findById(shop.getId()).get();
        boolean isPresent = StreamSupport.stream(occasionRepository.findAll().spliterator(), false)
                .anyMatch(occasion -> occasion.getName().equals("anniversaire"));
        assertTrue(isPresent);
        Occasion occasion2 =StreamSupport.stream(occasionRepository.findAll().spliterator(), false)
                .filter(occasion -> occasion.getName().equals("anniversaire")).findFirst().get();
        assertTrue(shopRepo.getOccasionIds().contains(occasion2.getId()));

    }


    @Test
    public void addOccasionAlreadyExist() throws Exception {
        shopInformationsModifier.addOccasion(shop.getId(),"anniversaire");
        Shop shopRepo = shopRepository.findById(shop.getId()).get();
        boolean isPresent = StreamSupport.stream(occasionRepository.findAll().spliterator(), false)
                .anyMatch(occasion -> occasion.getName().equals("anniversaire"));
        assertTrue(isPresent);
        Occasion occasion2 =StreamSupport.stream(occasionRepository.findAll().spliterator(), false)
                .filter(occasion -> occasion.getName().equals("anniversaire")).findFirst().get();
        assertTrue(shopRepo.getOccasionIds().contains(occasion2.getId()));


        shopInformationsModifier.addOccasion(shop.getId(),"anniversaire");
         isPresent = (StreamSupport.stream(occasionRepository.findAll().spliterator(), false)
                .filter(occasion -> occasion.getName().equals("anniversaire")).count())==1;
        assertTrue(isPresent);
    }


    @Test
    public void setUpOpeningHour() throws Exception {
        LocalTime localTime =LocalTime.of(10,10,10);
        shopInformationsModifier.setUpOpeningHour(shop.getId(), localTime);
        boolean isPresent = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .anyMatch(shop1 -> localTime==shop1.getOpeningHour());
        assertTrue(isPresent);
    }

    @Test
    public void setUpClosingHour() throws Exception {
        LocalTime localTime =LocalTime.of(10,10,10);
        shopInformationsModifier.setUpClosingHour(shop.getId(), localTime);
        boolean isPresent = StreamSupport.stream(shopRepository.findAll().spliterator(), false)
                .anyMatch(shop1 -> localTime==shop1.getOpeningHour());
        assertTrue(isPresent);
    }

    @Test
    public void addThemeToCook() throws Exception {
        shopInformationsModifier.addThemeToCook(cook.getId(),"fleur");
        Cook cookRepo = cookRepository.findById(cook.getId()).get();
        boolean isPresent = StreamSupport.stream(themeRepository.findAll().spliterator(), false)
                .anyMatch(occasion -> occasion.getName().equals("fleur"));
        assertTrue(isPresent);
        Theme theme2 =StreamSupport.stream(themeRepository.findAll().spliterator(), false)
                .filter(theme -> theme.getName().equals("fleur")).findFirst().get();
        assertTrue(cookRepo.getThemeIds().contains(theme2.getId()));

    }

    @Test
    public void addThemeAlreadyExistToCook() throws Exception {
        shopInformationsModifier.addThemeToCook(cook.getId(),"fleur");
        Cook cookRepo = cookRepository.findById(cook.getId()).get();
        boolean isPresent = StreamSupport.stream(themeRepository.findAll().spliterator(), false)
                .anyMatch(occasion -> occasion.getName().equals("fleur"));
        assertTrue(isPresent);
        Theme theme2 =StreamSupport.stream(themeRepository.findAll().spliterator(), false)
                .filter(theme -> theme.getName().equals("fleur")).findFirst().get();
        assertTrue(cookRepo.getThemeIds().contains(theme2.getId()));


        shopInformationsModifier.addThemeToCook(cook.getId(),"fleur");
        isPresent = (StreamSupport.stream(themeRepository.findAll().spliterator(), false)
                .filter(theme -> theme.getName().equals("fleur")).count())==1;
        assertTrue(isPresent);

    }



}
