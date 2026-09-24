package Domain;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private final String id;
    private String name;
    private  String district;
    private final Set<CuisineCategory> cuisineCategories;
    private double totalRating;
    private int ratingCount;
    private final Menu menu;
    private boolean status; //true>> open
    private int completedOrders;

    public Restaurant(String name,String district,Menu menu){
        id="RS-"+nextId.getAndIncrement();
        this.name=name;
        this .district=district;
        this.menu=menu;
        cuisineCategories=new HashSet<>();
        status=true;
        completedOrders=0;
    }

    public void incrementCompletedOrders() {
        completedOrders++;
    }


    public int getCompletedOrders() {
        return completedOrders;
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
