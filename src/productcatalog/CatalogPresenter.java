package productcatalog;

import java.util.ArrayList;
import java.util.List;

public class CatalogPresenter implements CatalogContract.UserActions {

    private final CatalogRepository catalogRepository;
    private final CatalogContract.View view;
    private ProductValidator productValidator;

    public CatalogPresenter(CatalogRepository repository, CatalogContract.View view) {
        this.catalogRepository = repository;
        this.view = view;
    }

    @Override
    public void addProduct(Product product) {
        productValidator = new ProductValidator(product);
        if (productValidator.isProductValid()) {
            catalogRepository.addProduct(product);
            view.closeAddProductDialog();
        } else {
            System.out.println("product not added " + productValidator.getErrorMessage());
            productValidator.getErrorMessage();
            view.displayErrorMessage(productValidator.getErrorMessage());
        }
    }

    @Override
    public void deleteProduct(String productIds) {
        catalogRepository.deleteProduct(productIds);
    }

    @Override
    public void modifyProduct(Product product, int productId) {
        productValidator = new ProductValidator(product);
        if (productValidator.isProductValid()) {
            catalogRepository.modifyProduct(product, productId);
        } else {
            productValidator.getErrorMessage();
            view.displayErrorMessage(productValidator.getErrorMessage());
            System.out.println("modifyProduct" + productValidator.getErrorMessage());
        }
    }

    public void getProductsFromDb() {
        view.displayProductTable(catalogRepository.getProductList());
    }

    public ArrayList<String> getCategoriesFromDb() {
        return catalogRepository.getCategoryList();
    }
}
