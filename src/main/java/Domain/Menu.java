package Domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Menu {
    private Map<String, MenuItem>items=new LinkedHashMap<>();

    public void addItem(MenuItem item){
        items.put(item.getId(),item);
    }
    public void removeItem(String itemId){
        items.remove(itemId);
    }
    public List<MenuItem> getItems(){
        return List.copyOf(items.values());
    }
    public MenuItem findById(String id){
        return items.get(id);
    }
}
