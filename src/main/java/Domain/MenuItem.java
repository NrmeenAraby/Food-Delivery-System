package Domain;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class MenuItem {
    private static int nextId=1;
    private final String id;
    private String name;
    private  ItemCategory itemCategory;
    private int preparationTimeMinutes;
    private boolean availabilityFlag;

    public MenuItem(String name, ItemCategory itemCategory, int preparationTimeMinutes) {
        id="I-"+Integer.toString(nextId++);
        this.name = name;
        this.itemCategory = itemCategory;
        this.preparationTimeMinutes = preparationTimeMinutes;
        availabilityFlag=true;
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

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public boolean isAvailabilityFlag() {
        return availabilityFlag;
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
