package Domain;

import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private final List<String> logs = new ArrayList<>();

    public void addEntry(String entry) {
        logs.add(entry);
    }

    public List<String> getLogs() {
        return List.copyOf(logs);
    }
    public void showLogs() {
        if (logs.isEmpty()) {
            System.out.println("No audit logs.");
            return;
        }

        logs.forEach(System.out::println);
    }
}