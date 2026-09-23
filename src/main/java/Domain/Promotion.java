package Domain;

import Exceptions.IllegalPromotionException;
import PromotionStrategies.PromotionStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Promotion {
    private final String code;
    private BigDecimal minimumSubTotal;
    private LocalDate expiryDate;
    private String restrictedDistrict;
    private boolean firstTimeCustomersRestriction;
    private PromotionStrategy promotionStrategy;

    public Promotion(String code, BigDecimal minimumSubTotal, LocalDate expiryDate,
                     boolean firstTimeCustomersRestriction,PromotionStrategy promotionStrategy){
        this(code,minimumSubTotal,expiryDate,null,firstTimeCustomersRestriction,promotionStrategy);
    }
    public Promotion(String code, BigDecimal minimumSubTotal, LocalDate expiryDate,String restrictedDistrict,
                     boolean firstTimeCustomersRestriction,PromotionStrategy promotionStrategy){
        this.code =code;
        this.minimumSubTotal=minimumSubTotal;
        this.expiryDate=expiryDate;
        this.restrictedDistrict = restrictedDistrict;
        this.firstTimeCustomersRestriction=firstTimeCustomersRestriction;
        this.promotionStrategy=promotionStrategy;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getMinimumSubTotal() {
        return minimumSubTotal;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getRestrictedDistrict() {
        return restrictedDistrict;
    }

    public boolean isFirstTimeCustomersRestriction() {
        return firstTimeCustomersRestriction;
    }

    public void isApplicable(BigDecimal subTotal, boolean firstTimeCustomer, String targetedDistrict){
        if(subTotal.compareTo(minimumSubTotal)<0) {
            throw new IllegalPromotionException("The subtotal less than minimum subtotal needed to apply this promotion");
        }
        if (expiryDate.isBefore(LocalDate.now())){
            throw new IllegalPromotionException("This promotion is expired");
        }
        if(firstTimeCustomersRestriction && !firstTimeCustomer){
            throw new IllegalPromotionException("This promotion is first time customer restricted");
        }
        if(restrictedDistrict !=null && !restrictedDistrict.equals(targetedDistrict)){
            throw new IllegalPromotionException("This promotion is restricted only to "+ restrictedDistrict);
        }
    }

    public PromotionStrategy getPromotionStrategy() {
        return promotionStrategy;
    }
}
