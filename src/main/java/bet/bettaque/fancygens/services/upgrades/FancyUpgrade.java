package bet.bettaque.fancygens.services.upgrades;

import bet.bettaque.fancygens.db.PlacedCustomBlock;
import bet.bettaque.fancygens.db.PlacedUpgradableChest;
import bet.bettaque.fancygens.services.FancyResource;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class FancyUpgrade {


    FancyResource costResource ;
    BigDecimal costAmount ;
    int level ;


//    public abstract void applyUpgrade();

    public abstract void applyUpgrade(PlacedCustomBlock placedCustomBlock);

//    public abstract void applyUpgrade(PlacedUpgradableChest placedUpgradableChest);


}
