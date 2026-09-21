package Domain;

import Exceptions.InvalidComboItemException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComboItem extends MenuItem{
    private Map<String, MenuItem> comboItems=new LinkedHashMap<>();
    private BigDecimal discount;

    public ComboItem(String name, ItemCategory itemCategory, int preparationTimeMinutes, BigDecimal discount) {
        super(name,itemCategory, preparationTimeMinutes);
        this.discount=discount;
    }

    public void addItem(MenuItem item){
        if(item instanceof ComboItem) {
            throw new InvalidComboItemException( "A combo cannot contain another combo");
        }
        comboItems.put(item.getId(), item);
    }
    public void removeItem(String itemId){
        comboItems.remove(itemId);
    }
    public List<MenuItem> getItems(){
        return List.copyOf(comboItems.values());
    }
    public MenuItem findById(String id){
        return comboItems.get(id);
    }

    @Override
    public BigDecimal calculateItemPrice() {
        BigDecimal total=BigDecimal.ZERO;
        for(var item:comboItems.values()){
            total=total.add(item.calculateItemPrice());
        }
        return total.multiply(discount);
    }
}
