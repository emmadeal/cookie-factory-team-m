package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.ShopInformationsModifier;
import fr.unice.polytech.cf.model.recipe.Occasion;
import fr.unice.polytech.cf.model.recipe.Theme;
import fr.unice.polytech.cf.model.shop.Cook;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.CookRepository;
import fr.unice.polytech.cf.repositories.OccasionRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import fr.unice.polytech.cf.repositories.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;


@Component
@RequiredArgsConstructor
public class ShopInformationService implements ShopInformationsModifier  {

    private final ShopRepository shopRepository;
    private final OccasionRepository occasionRepository;

    private final ThemeRepository themeRepository;

    private final CookRepository cookRepository;

    @Override
    public Shop fixTax(UUID shopId, double taxValue) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        shop.setTax(taxValue);
        shopRepository.save(shop, shop.getId());
        return shop;
    }


    @Override
    public Occasion addOccasion(UUID shopId, String name) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        Optional<Occasion> occasionOptional= StreamSupport.stream(occasionRepository.findAll().spliterator(), false)
                .filter(occasion -> name.equals(occasion.getName())).findAny();
        Occasion occasion;
        if(occasionOptional.isPresent()){
            occasion = occasionOptional.get();
        }else{
            occasion = new Occasion(name);
            occasionRepository.save(occasion,occasion.getId());
        }
        shop.getOccasionIds().add(occasion.getId());
        shopRepository.save(shop,shopId);
        return occasion;
    }

    @Override
    public void setUpOpeningHour(UUID shopId, LocalTime openingHour) throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        shop.setOpeningHour(openingHour);
        shopRepository.save(shop, shop.getId());
    }

    @Override
    public void setUpClosingHour(UUID shopId, LocalTime closingHour)throws ResourceNotFoundException {
        Shop shop = getShopById(shopId);
        shop.setOpeningHour(closingHour);
        shopRepository.save(shop, shop.getId());
    }

    @Override
    public Theme addThemeToCook(UUID cookId, String name) throws ResourceNotFoundException {
        Cook cook = cookRepository.findById(cookId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No cook found with given id %s", cookId.toString()))
        );
        Optional<Theme> themeOptional= StreamSupport.stream(themeRepository.findAll().spliterator(), false)
                .filter(theme -> name.equals(theme.getName())).findAny();

        Theme theme;
        if(themeOptional.isPresent()){
            theme = themeOptional.get();
        }else{
            theme = new Theme(name);
            themeRepository.save(theme,theme.getId());
        }
        cook.getThemeIds().add(theme.getId());
        cookRepository.save(cook,cookId);
        return theme;
    }

    public Shop getShopById(UUID shopId) throws ResourceNotFoundException {
        return shopRepository.findById(shopId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("No shop found with given id %s", shopId.toString()))
        );
    }
}
