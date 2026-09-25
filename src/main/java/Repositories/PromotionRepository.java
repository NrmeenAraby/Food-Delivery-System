package Repositories;


import Domain.Promotion;

import java.util.HashMap;

public class PromotionRepository {
    private final HashMap<String, Promotion> promotions=new HashMap<>();
    public void addPromotion(Promotion promotion){
        promotions.put(promotion.getCode().toUpperCase(),promotion);
    }
    public Promotion findByCode(String promotionCode){
        return promotions.get(promotionCode.toUpperCase());
    }

}
