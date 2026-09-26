package Domain;

import Exceptions.PlatformException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private final String id;
    private String name;
    private  String district;
    private final Set<CuisineCategory> cuisineCategories;
    private double avgRating;
    private final Menu menu;
    private boolean status; //true>> open
    private int completedOrders;

    public Restaurant(String name,String district, double avgRating){
        if(name.isBlank()){
            throw new PlatformException("Name cant be empty");
        }
        if(district.isBlank()){
            throw new PlatformException("District cant be empty");
        }
        id="RS-"+nextId.getAndIncrement();
        this.name=name;
        this .district=district;
        setRating(avgRating);
        this.menu=new Menu();
        cuisineCategories=new LinkedHashSet<>();
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

    public boolean getStatus() {
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

    public void setRating(double rating){
        if(rating<0 || rating>5){
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.avgRating=rating;
    }
    public double getAvgRating(){
        return avgRating;
    }
    public void updateRestaurantStatus(boolean status){
        this.status=status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return String.format(
                "Restaurant{id='%s', name='%s', district='%s', cuisines=%s, rating=%.1f, status=%s}",
                id,
                name,
                district,
                cuisineCategories,
                avgRating,
                isOpen() ? "OPEN" : "CLOSED"
        );
    }
}
