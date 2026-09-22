package Domain;

import java.math.BigDecimal;

public class SearchCriteria {
    private String district;
    private CuisineCategory cuisineCategory;
    private  double minimumRating;
    private BigDecimal priceCeiling;

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setCuisineCategory(CuisineCategory cuisineCategory) {
        this.cuisineCategory = cuisineCategory;
    }

    public void setMinimumRating(double minimumRating) {
        this.minimumRating = minimumRating;
    }

    public void setPriceCeiling(BigDecimal priceCeiling) {
        this.priceCeiling = priceCeiling;
    }

    public String getDistrict() {
        return district;
    }

    public CuisineCategory getCuisineCategory() {
        return cuisineCategory;
    }

    public double getMinimumRating() {
        return minimumRating;
    }

    public BigDecimal getPriceCeiling() {
        return priceCeiling;
    }
}
