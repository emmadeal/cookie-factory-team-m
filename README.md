# Cookiefactory-22-23-Team-M

  





## User stories 
La liste des fonctionnalités livrées par user story.

### Milestone 1
---

Proposer une recette #4  
**En tant que** Chef **Je veux** proposer une nouvelle recette à partir d'ingrédients disponibles **afin que** le manager puisse la valider ou non.  
Fichier cucumber : **submitRecipe.feature** sous resources/features/factoryRessource  
Scénario : 
- Soumettre une recette  
  
  &nbsp;

Valider une recette #5  
**En tant que** manager, **Je veux** valider ou non la recette soumise **afin de** la faire ajouter aux recettes disponibles sur le site.  
Fichier cucumber : **validateRecipe.feature** sous resources/features/addingRecipe  
Scénario : 
- validating a recipe  
- no validating a recipe beacause of the name  
  
&nbsp;

Ajouter une recette #6  
**En tant que** chef, **Je veux** récupérer la validation du manager et ajouter la recette sur le site  **afin qu’un** internaute puisse la sélectionner.  
Fichier cucumber : **addRecipe.feature** sous resources/features/factoryRessource  
Scénario : 
- recipe validated  
  
&nbsp;

Préparer une commande #7   
**En tant que** cuisinier, **je veux** récupérer la commande validée et commence à la préparer **afin** qu’elle soit prête à être récupérée par le client.  
Fichier cucumber : **cookPrepareOrder.feature** sous resources/features/cookingTime     
Scénario : 
- the cook starts cooking the order
- the cook finished cooking the order  

 &nbsp;
 
Commander un cookie #8   
**En tant qu'** internaute, **je veux** choisir un cookie, puis un magasin, et récupérer le prix total de ma commande **afin de** la valider.  
Fichier cucumber : **clientOrderRecipe.feature** sous resources/features/ordering   
Scénario : 
- a client add recipe in this basket  
- a client delete recipe in this basket  
  
  &nbsp;
  
Récupérer la commande #9   
En tant que cuisinier, je change l’état de la commande en « récupérée » quand elle a été récupérée afin que tout le monde soit informé qu’il n’y a plus d’action à réaliser sur cette commande.  
Fichier cucumber : **clientOrderRecipe.feature** sous resources/features/ordering   
Scénario : 
- a client pick up his order  
  
  &nbsp;
  
### Milestone 2
---

Limite ingrédients dans une recette 👩🏻‍🍳🛒 #27   
**En tant que** Chef, **Je veux** créer une nouvelle recette **afin de** de la proposer aux futurs clients tout en respectant un squelette définis et en choisissant des ingrédients existants.  
Fichier cucumber : **validateRecipe.feature** sous resources/features/addingRecipe  
Scénario : 
- no validating a recipe because of the ingredients  

  &nbsp;
  
Vérifier tout le setup avant qu'une commande démarre #29  
**En tant qu**’utilisateur lorsque je fait une commande **je veux que** la cookie factory soit ouverte et qu’au moins un magasin lui soit assigné ainsi que des recette et des employées  
Fichier cucumber : **clientOrderRecipe.feature** sous resources/features/ordering  
Scénario : 
- a client begin an order :  choose shop  
- a client add recipe in this basket  
- a client choose a pickup hour  
- a client validate the order  
  
  &nbsp;
  
Ajout de la taxe #30  
**En tant que** ShopManager, **je veux** ajouter une taxe de prix sur les produits vendus dans mon magasin **afin que** le prix de chaque commande soit correct.  
Fichier cucumber : **shopConfig.feature** sous resources/features/managingShop  
Scénario : 
- set tax  
  
  &nbsp;
  
Annulation de commande #32  
**En tant que** client, **je veux** pouvoir annuler ma commande **afin de** ne pas la récupérer  
Fichier cucumber : **clientCancelOrder.feature** sous resources/features/ordering  
Scénario : 
- a client cancel his order  
- a client can't cancel his order because the order is not paid  
- a client can't cancel his order because the cook has started
  
  &nbsp;
  
Associer un cuisinier #33  
**En tant que** client, **Je veux** choisir une heure de retrait **afin de** pouvoir associer un cuisinier disponible à cette horaire  
**En tant que** shopManager, **Je veux** associer un cuisinier à la commande **afin de** pouvoir la préparer pour l'heure indiquée  
Fichier cucumber : **assignOrderToCook.feature** sous resources/features/shopAction  
Scénario : 
- The shop assign the order to the only available cook (Sprint 2)  
- The shop assign the order to one of the available cooks (Sprint 2)
- The shop doesn't have an available cook for the hour (Sprint 2)  
  
  &nbsp;
  
Gestion du stock #34  
**En tant que** ShopManager, **je veux** pouvoir indiquer mon stock de recette initial **afin de** connaître le nombre de cookie disponible à la vente  
**En tant que** ShopManager, **je veux** décrémenter le stock de recette disponible dans mon magasin lorsqu'un client en commande un **afin de** ne pas proposer aux autres clients une recette indisponible  
Fichier cucumber : **manageStock.feature** sous resources/features/managingShop  
Scénario : 
- add cookies to the stock  
- first client choose one cookie  
- first client choose all the cookies in stock  
- second client choose one cookie but stock is empty  
- the first client remove a cookie from his basket  
- the second client choose one cookie again  
  
  &nbsp;
  
### Milestone 3
---

Commande obsolète #36  
**En tant que** cuisinier, **Je veux** vérifier le temps passé depuis l'heure de retrait choisie **afin de** passé la commande à l'état obsolète si besoin  
Fichier cucumber : **userNoPickUpOrder.feature** sous resources/features/obsoleting  
Scénario : 
- the order is obsolete  
  
  &nbsp;
  
Prise en compte du temps de préparation 👩‍🍳 #55  
**En tant que** cuisinier, **Je veux** avoir un temps de préparation pour chaque commande, découpé en tranches de 15 minutes **afin de** pouvoir savoir quand je suis disponible ou occupé par une commande  
**En tant que** shopManager, **Je veux** associer un cuisinier disponible à la commande **afin de** pouvoir la préparer pour l'heure indiquée  
Fichier cucumber : **assignOrderToCook.feature** sous resources/features/shopAction    
Scénario : 
- The shop assign the order to the only available cook (Sprint 3)  
- The shop assign the order to one of the available cooks (Sprint 3)  
- The shop doesn't have an available cook for the hour (Sprint 3)  
Fichier cucumber : **timeNeededToCook.feature** sous resources/features/cookingTime    
Scénario : 
- The order takes less than 15 minutes  
- The order takes less than 30 minutes but more than 15  
- The order takes more than 1 hour    
  
  &nbsp;
  
Notifier le client 🤳🏼 #56  
**En tant que** client, **Je veux** etre notifier par le cuisinier  **afin de**de ne pas oublier de récupérer ma commande  
Fichier cucumber : **userNoPickUpOrder.feature** sous resources/features/obsoleting  
Scénario : 
- the user is notify 5 minutes
- the user is notify 1 hour
  
  &nbsp;
  
Ajout des horaires d'ouvertures du magasin 🏪 #58  
**En tant que** shopManager, **Je veux** ajouter et modifier les horaires d'ouverture de mon magasin **afin que** les commandes n'arrivent pas 24h/24  
**En tant que** cuisinier, **Je veux** travailler que sur les horaires d'ouverture du magasin auquel je suis affiliée **afin de** savoir quand est-ce que je travaille  
**En tant que** client **Je veux** choisir une horaire de retrait pour ma commande qui entre dans les horaires de la boutique **afin de** pouvoir commander  
Fichier cucumber : **configShop.feature** sous resources/features/managingShop    
Scénario : 
- set closing time  
- set opening time  
  
  &nbsp;
  
Quand un client complète une commande il peut créer un compte 💻 #59  
**En tant qu'** internaute👩‍💻 , **Je veux** créer un compte à la fin de ma commande **afin de** d'enregistrer mes informations.  
Fichier cucumber : **userAuth.feature** sous resources/features/authentification    
Scénario :   
- user sign in  
  
  &nbsp;
  
Vérification du prix 💸 #60  
**En tant que** factoryManager, **Je veux** pouvoir analyser le prix d'une recette soumise **afin de** choisir de la valider ou non  
Fichier cucumber : **validateRecipe.feature** sous resources/features/addingRecipe  
Scénario : 
- validateRecipe.feature  
  
  &nbsp;
  
### Milestone 4
---  
   
Ajout de l'adhésion #78  
**En tant que** user, **Je veux** pouvoir bénéficier d'un discount de 10% sur ma prochaine commande après 30 cookies commandés **afin de** payer moins chère  
Fichier cucumber : **userJoinProgram.feature** sous resources/features/joiningProgram  
Scénario : 
- a user join the loyalty program  
- the user don't have reduction on his order  
- the user have reduction on his order  
  
  &nbsp;
  
Connexion du client 🫡 #80  
**En tant que** client, **Je veux** pouvoir me connecter **afin de**d'avoir un service plus personnalisé   
Fichier cucumber : **userAuth.feature** sous resources/features/authentification    
Scénario : 
- user log in  
  
  &nbsp;
  
Créer une nouvelle recette 👩🏼‍🍳 #81  
**En tant que** chef de la factory, **Je veux** créer une nouvelle recette **afin de** la soumettre au manager  
Fichier cucumber : **createRecipe.feature** sous resources/features/addingRecipe  
Scénario : 
- a chef create a recipe named "superCookie"
- a chef create a recipe named "superCookie" and a recipe named "chocolateCookie"  
  
  &nbsp;
  
Ajout d'une nouvelle recette tous les mois 🔪 #82  
**En tant que** Chef, **Je veux** être alerté chaque début de mois **afin de** confectionner une nouvelle recette pour diversifier le choix de cookies de l'enseigne.  
**En tant que** Factory Manager, **Je veux** être alerté chaque début de mois **afin de** valider les nouvelles recettes à être ajoutés à la liste des recettes de l'enseigne.  
Fichier cucumber : **monthlyRecipe.feature** sous resources/features/factoryRessource  
Scénario : 
- monthly recipe  
  
  &nbsp;
  
Inscription du client #84  
**En tant que** client, **Je veux** pouvoir m'inscrire **afin**d'avoir un service plus personnalisé  
Fichier cucumber : **userAuth.feature** sous resources/features/authentification    
Scénario :  
- user log in  
  
  &nbsp;
  
Suppression de recette #85  
**En tant que** Factory Manager, **Je veux** consulter le nombre de commandes passés selon la recette  **afin de** les supprimer si elles ne sont pas commandés.  
Fichier cucumber : **monthlyRecipe.feature** sous resources/features/factoryRessource    
Scénario : 
- monthly recipe    
  
  &nbsp;
  
Création de panier surprise #86  
**En tant que** chef de magasin, **Je veux** pouvoir faire des panier surprise fait avec les commandes non récupérées **afin**de les proposer au clients et de ne pas les gaspiller  
**En tant que** chef de magasin, **Je veux** ajouter des paniers surprises sur tooGoodToGo **afin**de savoir quand un client en réserve un  
**En tant que** chef de magasin, **Je veux** informer tooGoodToGo lorsqu'un panier est récupérer **afin** que tooGoodToGo puisse valider le paiement  
Fichier cucumber : **tooGoodToGo.feature** sous resources/features/tooGoodToGo  
Scénario : 
- Un client réserve un panier déja réservé
- Un client reserve un panier disponible  
- Le client va récupérer son panier surprise  
  
  &nbsp;
  
Add "Personalized Cookies for your Party" #87  
**En tant que** client, **Je veux** pouvoir commander de très gros cookies en spécifiant la date, l'heure le magasin de retrait **afin de** fêter une occasion avec de bons cookies.  
**En tant que** shopManager, **Je veux** savoir si un chef de mon magasin est capable de réaliser des cookies personnalisés **afin de** savoir si je propose l'option à mes clients.  
**En tant que** shopManager, **Je veux** proposer différents thèmes et occasions pour mes cookies personnalisés en fonction des chefs présents dans le magasin **afin de** proposer beaucoup de personnalisations à mes clients.  
**En tant que** shopManager, **Je veux** appliquer une marge supplémentaire de 25% lors de la commande de cookies personnalisés **afin de** compenser le travail fournit. 
**En tant que** shopManager, **Je veux** multiplier le prix d'un cookie de taille supérieur **afin de** compenser les ingrédients fournits.  
Fichier cucumber : **assignOrderToCook.feature** sous resources/features/shopAction  
Scénario : 
- The shop assign the order to the only available cook (Sprint 4)  
- The shop doesn't have an available cook for the hour (Sprint 4)  
  
  &nbsp;
  
### Milestone 5
---  

Ajout notification paniers surprise #105  
**En tant que** client tooGoodToGo**Je veux** etre notifier si des paniers surprise sont disponible par mail**afin**de ne pas rater de bonnes occasion  
Fichier cucumber : **tooGoodToGo.feature** sous resources/features/tooGoodToGo  
Scénario : 
- A new surprise basket is available  

