package fr.unice.polytech.cf.component;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.model.factory.Catalog;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.repositories.CatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Slf4j
@RequiredArgsConstructor
public class CatalogCreation implements CatalogController {

    private final CatalogRepository catalogRepository;


    @Override
    public void addNewIngredient(Ingredient ingredient) {
        catalogRepository.save(ingredient, ingredient.getId());
    }

    @Override
    public void registerCatalog() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")
                    + "/src/main/resources/catalog.json")));
            Catalog catalog = mapper.readValue(jsonString, Catalog.class);
            for(Ingredient ingredient : catalog){
                addNewIngredient(ingredient);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
