package productcatalog;

import java.util.ArrayList;

public interface CatalogRepository {

    void addProduct(Product product);

    void modifyProduct(Product product, int productId);

    void deleteProduct(String productIds);

    ArrayList<Product> getProductList(int requestedPage, int itemsPerPage);

    ArrayList<String> getCategoryList();

    ArrayList<Product> searchProduct(Search search);
    
    int getProductNumber();
    
    int getPageCount(int productsPerPage);

    public void goToPage(int requestedPage, int itemsPerPage);

}
