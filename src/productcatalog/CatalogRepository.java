package productcatalog;

import java.util.ArrayList;

public interface CatalogRepository {

    void addProduct(Product product);

    void modifyProduct(Product product, int productId);

    void deleteProduct(String productIds);

    ArrayList<Product> getProductList();

    public ArrayList<String> getCategoryList();

}
