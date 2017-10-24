package productcatalog;

import java.util.Date;

public class Search {

    private final String name, color, category;
    private final Double lowerPrice, higherPrice;
    private final Date lowerExpiringDate, higherExpiringDate;
    private final boolean inStock;

    private Search(SearchBuilder searchBuilder) {
        this.name = searchBuilder.name;
        this.color = searchBuilder.color;
        this.category = searchBuilder.category;
        this.lowerPrice = searchBuilder.lowerPrice;
        this.higherPrice = searchBuilder.higherPrice;
        this.lowerExpiringDate = searchBuilder.lowerExpiringDate;
        this.higherExpiringDate = searchBuilder.higherExpiringDate;
        this.inStock = searchBuilder.inStock;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getCategory() {
        return category;
    }

    public Double getLowerPrice() {
        return lowerPrice;
    }

    public Double getHigherPrice() {
        return higherPrice;
    }

    public Date getLowerDate() {
        return lowerExpiringDate;
    }

    public Date getHigherDate() {
        return higherExpiringDate;
    }

    public boolean getInStock() {
        return inStock;
    }

    public static class SearchBuilder {

        String name, color, category;
        Double lowerPrice, higherPrice;
        Date lowerExpiringDate, higherExpiringDate;
        boolean inStock;

        public SearchBuilder name(String name) {
            if (name != null) {
                this.name = name;
            }
            return this;
        }

        public SearchBuilder color(String color) {
            if (color != null) {
                this.color = color;
            }
            return this;
        }

        public SearchBuilder category(String category) {
            if (category != null) {
                this.category = category;
            }
            return this;
        }

        public SearchBuilder lowerPrice(Double lowerPrice) {
            if (lowerPrice != null) {
                this.lowerPrice = lowerPrice;
                return this;
            }
            return this;
        }

        public SearchBuilder higherPrice(Double higherPrice) {
            if (higherPrice != null) {
                this.higherPrice = higherPrice;
            }
            return this;
        }

        public SearchBuilder lowerExpiringDate(Date lowerExpiringDate) {
            if (lowerExpiringDate != null) {
                this.lowerExpiringDate = lowerExpiringDate;
            }
            return this;
        }

        public SearchBuilder higherExpiringDate(Date higherExpiringDate) {
            if (higherExpiringDate != null) {
                this.higherExpiringDate = higherExpiringDate;
            }
            return this;
        }

        public SearchBuilder inStock(boolean inStock) {
            this.inStock = inStock;
            return this;
        }

        public Search build() {
            return new Search(this);
        }
    }
}
