package productcatalog;

public class ProductValidator {

    private final Product product;
    StringBuilder error = new StringBuilder();

    public ProductValidator(Product product) {
        this.product = product;
    }

    private void validateProduct() {

        if (TextUtils.isEmpty(product.getName())) {
            error.append("name, ");
        }

        if (product.getPrice() <= 0) {
            error.append("price, ");
        }

        if (product.getColor() == null) {
            error.append("color, ");
        }

        if (product.getExpiringDate() == null) {
            error.append("expiring date, ");
        }

        if (product.getCategoryName() == null) {
            error.append("category name");
        }
    }

    public boolean isProductValid() {
        validateProduct();
        return error != null && error.length() <= 0;
    }

    public String getErrorMessage() {
        System.out.println("getError " + error);
        return error.toString();
    }

}
