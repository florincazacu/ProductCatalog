package productcatalog;

public class Category {

    private final int id;
    private final String name;

    public Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
    }

    public int getProductTypeId() {
        return id;
    }

    public String getProductTypeName() {
        return name;
    }

}
