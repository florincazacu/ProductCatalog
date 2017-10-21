package productcatalog;

import java.util.ArrayList;

public class CatalogContract {

    public interface UserActions {

        void addProduct(Product product);

        void deleteProduct(String productIds);

        void modifyProduct(Product product, int productId);

    }

    public interface View {

        void displayProductTable(ArrayList<Product> products);

        void displayAddProductForm();

        void displayErrorMessage(String errorMessage);

        void closeAddProductDialog();

    }

}
