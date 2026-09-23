package Domain;

import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private final List<String> entries = new ArrayList<>();

    public void addEntry(String entry) {
        entries.add(entry);
    }

    public List<String> getEntries() {
        return List.copyOf(entries);
    }
}