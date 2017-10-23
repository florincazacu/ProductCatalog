package productcatalog;

import java.util.ArrayList;

public class CatalogContract {

    public interface UserActions {

        void addProduct(Product product);

        void deleteProduct(String productIds);

        void modifyProduct(Product product, int productId);
        
        void searchProduct(Search search);

    }

    public interface View {

        void displayProductsTable(ArrayList<Product> products);

        void displayAddProductForm();

        void displayErrorMessage(String errorMessage);

        void closeAddProductDialog();

        void displayFoundProductsNumber(int productNumber);

    }

}
