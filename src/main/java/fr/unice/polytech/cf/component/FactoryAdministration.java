package fr.unice.polytech.cf.component;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.interfaces.CatalogController;
import fr.unice.polytech.cf.interfaces.FactoryHumanRessources;
import fr.unice.polytech.cf.interfaces.FactoryRessources;
import fr.unice.polytech.cf.model.factory.Chef;
import fr.unice.polytech.cf.model.factory.FactoryManager;
import fr.unice.polytech.cf.model.shop.Shop;
import fr.unice.polytech.cf.repositories.ChefRepository;
import fr.unice.polytech.cf.repositories.FactoryManagerRepository;
import fr.unice.polytech.cf.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FactoryAdministration implements FactoryHumanRessources, FactoryRessources {

    private final ChefRepository chefRepository;

    private final FactoryManagerRepository factoryManagerRepository;

    private final ShopRepository shopRepository;

    private final CatalogController catalogController;


    @Override
    public Chef hireChef(String name){
        Chef chef = new Chef(name);
        chefRepository.save(chef,chef.getId());
        return chef;
    }

    @Override
    public void fireChef(Chef chef) throws ResourceNotFoundException {
        if(chefRepository.existsById(chef.getId())){
            chefRepository.deleteById(chef.getId());
            return;
        }
        throw new ResourceNotFoundException("no chef with given id");
    }

    @Override
    public FactoryManager hireFactoryManager(String name) {
        FactoryManager factoryManager  = new FactoryManager(name);
        factoryManagerRepository.save(factoryManager,factoryManager.getId());
        return factoryManager;
    }

    @Override
    public void fireFactoryManager(FactoryManager factoryManager) throws ResourceNotFoundException {
        if(factoryManagerRepository.existsById(factoryManager.getId())){
            factoryManagerRepository.deleteById(factoryManager.getId());
            return;
        }
        throw new ResourceNotFoundException("no factory manager with given id");
    }

    @Override
    public Shop addShop(String city) {
        Shop shop  = new Shop(city);
        shopRepository.save(shop,shop.getId());
        return shop;
    }

    @Override
    public void addIngredientsToCatalog() {
        catalogController.registerCatalog();
    }
}
