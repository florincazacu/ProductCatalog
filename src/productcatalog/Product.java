package productcatalog;

import java.util.Date;

public class Product {

    private int id;
    private String name;
    private double price;
    private String color;
    private boolean inStock;
    private Date expiringDate;
    private String categoryName;
    private int categoryId;

    private Product(ProductBuilder productBuilder) {
        try {
            this.id = productBuilder.id;
            this.name = productBuilder.name;
            this.price = productBuilder.price;
            this.color = productBuilder.color;
            this.inStock = productBuilder.inStock;
            this.expiringDate = productBuilder.expiringDate;
            this.categoryName = productBuilder.categoryName;
            this.categoryId = productBuilder.categoryId;
        } catch (Exception e) {
            System.out.println("Create product " + e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }

    public boolean getInStock() {
        return inStock;
    }

    public Date getExpiringDate() {
        return expiringDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return "id " + getId() + " name " + getName() + " price " + getPrice() + " color " + getColor() + " in stock " + getInStock()
                + " expiring date " + getExpiringDate() + " category name " + getCategoryName() + " category id " + getCategoryId();
    }

    public static class ProductBuilder {

        private int id;
        private String name;
        private double price;
        private String color;
        private boolean inStock;
        private Date expiringDate;
        private String categoryName;
        private int categoryId;

        public ProductBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public ProductBuilder color(String color) {
            this.color = color;
            return this;
        }

        public ProductBuilder inStock(boolean inStock) {
            this.inStock = inStock;
            return this;
        }

        public ProductBuilder expiringDate(Date expiringDate) {
            this.expiringDate = expiringDate;
            return this;
        }

        public ProductBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public ProductBuilder categoryId(int categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Product build() {
            return new Product(this);
        }

    }
}
