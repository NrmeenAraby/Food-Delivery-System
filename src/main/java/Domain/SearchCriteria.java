package Domain;

import java.math.BigDecimal;

public class SearchCriteria {
    private String district;
    private CuisineCategory cuisineCategory;
    private  Double minimumRating;
    private BigDecimal priceCeiling;
    private String keyword;
    public void setDistrict(String district) {
        this.district = district;
    }

    public void setCuisineCategory(CuisineCategory cuisineCategory) {
        this.cuisineCategory = cuisineCategory;
    }

    public void setMinimumRating(Double minimumRating) {
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

    public Double getMinimumRating() {
        return minimumRating;
    }

    public BigDecimal getPriceCeiling() {
        return priceCeiling;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
