package Repositories;


import Domain.Promotion;

import java.util.HashMap;

public class PromotionRepository {
    private HashMap<String, Promotion> promotions=new HashMap<>();
    public void addCustomer(Promotion promotion){
        promotions.put(promotion.getCode(),promotion);
    }
    public Promotion findByCode(String promotionCode){
        return promotions.get(promotionCode);
    }

}
