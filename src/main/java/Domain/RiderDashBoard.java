package Domain;

import java.util.ArrayList;
import java.util.List;

public class RiderDashBoard {
    private final List<String> entries = new ArrayList<>();
    public void addEntry(String message) {
        entries.add(message);
    }
    public List<String> getEntries() {
        return List.copyOf(entries);
    }

}
