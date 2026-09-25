package Services;

import Domain.Promotion;
import Exceptions.PlatformException;
import PromotionStrategies.PromotionStrategy;
import Repositories.PromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }
    public void createPromotion(String code, BigDecimal minimumSubTotal, LocalDate expiryDate, String restrictedDistrict,
                                boolean firstTimeCustomersRestriction, PromotionStrategy promotionStrategy){

        if (code == null || code.isBlank()) {
            throw new PlatformException("Promotion code cannot be empty.");
        }

        if (minimumSubTotal == null || minimumSubTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new PlatformException("Minimum subtotal cannot be negative.");
        }

        if (expiryDate == null) {
            throw new PlatformException("Expiry date cannot be null.");
        }

        if (expiryDate.isBefore(LocalDate.now())) {
            throw new PlatformException("Expiry date cannot be in the past.");
        }

        if (promotionStrategy == null) {
            throw new PlatformException("Promotion strategy cannot be null.");
        }

        if (promotionRepository.findByCode(code) != null) {
            throw new PlatformException("A promotion with this code already exists.");
        }

        Promotion promotion=new Promotion(code,minimumSubTotal,expiryDate,restrictedDistrict,
                firstTimeCustomersRestriction,promotionStrategy);
        promotionRepository.addPromotion(promotion);
    }
}
