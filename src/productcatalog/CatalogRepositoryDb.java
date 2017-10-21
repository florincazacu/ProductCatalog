package productcatalog;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CatalogRepositoryDb implements CatalogRepository {

    private Connection connection;
    private final String url = "jdbc:derby://localhost:1527/catalog_database";
    private final String username = "username";
    private final String password = "password";
    private final String productCatalogDb = "product_catalog";
    private final String productCategoryDb = "product_category";
    private final String[] categories = {null, "Electronics", "Furniture", "Toys"};
    private final ArrayList<String> categoryNames = new ArrayList<>();

    public CatalogRepositoryDb() {
        createCatalogTableIfNotExists();
        createProductTableIfNotExists();
        createCategoryList();
    }

    private void createCatalogTableIfNotExists() {
        connectToDb();
        try {
            String sqlCreateProductCategory = "CREATE TABLE USERNAME.PRODUCT_CATEGORY("
                    + "category_id      INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "category_name    VARCHAR(50)"
                    + ")";
            ResultSet productCategoryResultSet = connection.getMetaData().getTables(null, "USERNAME", "PRODUCT_CATEGORY", null);
            if (!productCategoryResultSet.next()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateProductCategory)) {
                    preparedStatement.executeUpdate(sqlCreateProductCategory);
                    addCategory();
                    productCategoryResultSet.close();
                }
                disconnectFromDb();
            }
        } catch (Exception e) {
            System.out.println("product category " + e.getMessage());
        }
    }

    private void createProductTableIfNotExists() {
        connectToDb();
        try {
            String sqlCreateProductCatalog = "CREATE TABLE USERNAME.PRODUCT_CATALOG("
                    + "product_id       INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "name             VARCHAR(50),"
                    + "price            DOUBLE,"
                    + "color            VARCHAR(20),"
                    + "availability     BOOLEAN,"
                    + "expiring_date    DATE,"
                    + "category_name    VARCHAR(20),"
                    + "category_id      INTEGER,"
                    + "FOREIGN KEY (category_id) REFERENCES product_category(category_id)"
                    + ")";
            ResultSet productCatalogResultSet = connection.getMetaData().getTables(null, "USERNAME", "PRODUCT_CATALOG", null);
            if (!productCatalogResultSet.next()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateProductCatalog)) {
                    preparedStatement.executeUpdate(sqlCreateProductCatalog);
                    System.out.println("product catalog created");
                    productCatalogResultSet.close();
                }
                disconnectFromDb();
            }

        } catch (Exception e) {
            System.out.println("product catalog " + e.getMessage());
        }
    }

    private void connectToDb() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            connection = DriverManager.getConnection(
                    url,
                    username,
                    password
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void disconnectFromDb() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE
        );
    }

    @Override
    public void addProduct(Product product) {
        connectToDb();
        String addProductSQL = "INSERT INTO " + productCatalogDb
                + "(NAME, PRICE, COLOR, AVAILABILITY, EXPIRING_DATE, CATEGORY_ID) VALUES"
                + "(?,?,?,?,?,?)";
        try {

            System.out.println(product.getName() + " " + product.getPrice() + " " + product.getColor() + " " + product.getInStock() + " " + product.getExpiringDate() + " " + product.getCategoryId());
            try (PreparedStatement preparedStatement = connection.prepareStatement(addProductSQL)) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setDouble(2, product.getPrice());
                preparedStatement.setString(3, product.getColor());
                preparedStatement.setBoolean(4, product.getInStock());
                preparedStatement.setDate(5, new java.sql.Date(product.getExpiringDate().getTime()));
                preparedStatement.setInt(6, product.getCategoryId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            disconnectFromDb();
        } catch (SQLException e) {
            System.out.println("add product " + e.getMessage());
        }
    }

    private void addCategory() {
        for (int i = 1; i < categories.length; i++) {
            try (ResultSet productCategoryResultSet = createStatement().executeQuery("SELECT * FROM " + productCategoryDb)) {
                productCategoryResultSet.moveToInsertRow();
                productCategoryResultSet.updateString("category_name", categories[i]);
                productCategoryResultSet.insertRow();
                productCategoryResultSet.close();
            } catch (SQLException e) {
                System.out.println("add product " + e.getMessage());
            }
        }
    }

    @Override
    public void modifyProduct(Product product, int productId) {
        String sql = "UPDATE " + productCatalogDb + " SET name = ?, price = ?, color = ?, availability = ?, expiring_date = ?, category_id = ? WHERE product_id = ?";
        try {
            connectToDb();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setDouble(2, product.getPrice());
                preparedStatement.setString(3, product.getColor());
                preparedStatement.setBoolean(4, product.getInStock());
                preparedStatement.setDate(5, (new java.sql.Date(product.getExpiringDate().getTime())));
                preparedStatement.setInt(6, product.getCategoryId());
                preparedStatement.setInt(7, productId);
                preparedStatement.executeUpdate();
            }
            disconnectFromDb();
        } catch (SQLException e) {
            System.out.println("modifyProduct" + e.getMessage());
        }
    }

    @Override
    public void deleteProduct(String productIds) {
        connectToDb();

        String sql = "DELETE FROM " + productCatalogDb + " WHERE product_id IN (" + productIds + ")";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            disconnectFromDb();
        } catch (SQLException e) {
            System.out.println("deleteProduct in DB " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Product> getProductList() {
        connectToDb();
        ArrayList<Product> productList = new ArrayList<>();
        String name, color, categoryName;
        double price;
        boolean availability;
        Date expiringDate;
        int categoryId, productId;
        String query = "SELECT * FROM " + productCategoryDb + " JOIN " + productCatalogDb + " ON ( "
                + productCategoryDb + ".category_id = " + productCatalogDb + ".category_id)";

        try (ResultSet resultSet = createStatement().executeQuery(query)) {
            while (resultSet.next()) {
                productId = resultSet.getInt("product_id");
                name = resultSet.getString("name");
                price = resultSet.getDouble("price");
                color = resultSet.getString("color");
                availability = resultSet.getBoolean("availability");
                expiringDate = resultSet.getDate("expiring_date");
                categoryName = resultSet.getString("category_name");
                categoryId = resultSet.getInt("category_id");
                Product product = new Product.ProductBuilder()
                        .id(productId)
                        .name(name)
                        .price(price)
                        .color(color)
                        .inStock(availability)
                        .expiringDate(expiringDate)
                        .categoryName(categoryName)
                        .categoryId(categoryId)
                        .build();
                productList.add(product);
            }

            disconnectFromDb();
        } catch (Exception e) {
            System.out.println("getProductList " + e);
        }
        return productList;
    }

    private void createCategoryList() {
        categoryNames.add("-ANY-");
        String categoryName;
        String query = "SELECT * FROM " + productCategoryDb;
        try (ResultSet resultSet = createStatement().executeQuery(query)) {
            while (resultSet.next()) {
                categoryName = resultSet.getString("category_name");
                categoryNames.add(categoryName);
            }
        } catch (Exception e) {
            System.out.println("getCategoryList " + e);
        }
    }

    @Override
    public ArrayList getCategoryList() {
        return categoryNames;
    }

}
