package Domain;

import Exceptions.StockShortageException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MenuItem {
    private static AtomicInteger nextId= new AtomicInteger(1);
    private final String id;
    private String name;
    private  ItemCategory itemCategory;
    private int preparationTimeMinutes;
    private boolean availabilityFlag;
    private double stockQuantity;

    public MenuItem(String name, ItemCategory itemCategory, int preparationTimeMinutes,double stockQuantity) {
        id="I-"+nextId.getAndIncrement();
        this.name = name;
        this.itemCategory = itemCategory;
        this.preparationTimeMinutes = preparationTimeMinutes;
        availabilityFlag=true;
        setStockQuantity(stockQuantity);
    }

    public void setStockQuantity(double stockQuantity) {
        if(stockQuantity>0)
           this.stockQuantity = stockQuantity;
        else
            throw new IllegalArgumentException("Cant add item with a stock quantity less than or equal 0");
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public boolean isAvailable() {
        return availabilityFlag && stockQuantity>0;
    }

    public boolean hasEnoughStock(double quantity){
        return stockQuantity>=quantity;
    }

    public void decreaseStock(double quantity){
        if(!hasEnoughStock(quantity)){
            throw new StockShortageException("Current stock quantity is "+this.stockQuantity);
        }
        this.stockQuantity-=quantity;
    }


    public void updateAvailability(boolean availabilityFlag){
        this.availabilityFlag=availabilityFlag;
    }
    public abstract BigDecimal calculateItemPrice();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(id, menuItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
