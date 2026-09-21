package Domain;

import java.util.HashSet;
import java.util.Set;

public class Restaurant {

    private static int nextId = 1;
    private final String id;
    private String name;
    private  String district;
    private Set<CuisineCategory> cuisineCategories;
    private double totalRating;
    private int ratingCount;
    private Menu menu;
    private boolean status; //true>> open

    public Restaurant(String name,String district,Menu menu){
        id="R-"+Integer.toString(nextId++);
        this.name=name;
        this .district=district;
        this.menu=menu;
        cuisineCategories=new HashSet<>();
        status=true;
    }

    public static int getNextId() {
        return nextId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public Menu getMenu() {
        return menu;
    }

    public boolean isOpen() {
        return status;
    }

    public Set<CuisineCategory> getCuisineCategories() {
        return Set.copyOf(cuisineCategories);
    }

    public void addCuisineCategory(CuisineCategory cuisineCategory){
        cuisineCategories.add(cuisineCategory);
    }
    public void removeCuisineCategory(CuisineCategory cuisineCategory){
        cuisineCategories.remove(cuisineCategory);
    }

    public void addRating(double rating){
        if(rating<0 || rating>5){
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        totalRating+=rating;
        ratingCount++;
    }
    public double getAvgRating(){
        return ratingCount==0? 0.0:totalRating/ratingCount;
    }
    public void updateRestaurantStatus(boolean status){
        this.status=status;
    }

}
