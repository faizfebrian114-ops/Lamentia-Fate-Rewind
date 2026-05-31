package lamentia;

public class Item {
    private String id;
    private String name;
    private String type; // "KARTU" atau "ARTEFAK"
    private String description;

    public Item(String id, String name, String type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    // Getter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
}
