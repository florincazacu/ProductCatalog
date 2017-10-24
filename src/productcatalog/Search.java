package productcatalog;

import java.util.Date;

public class Search {

    private final String name, color, category;
    private final Double lowerPrice, higherPrice;
    private final Date lowerDate, higherDate;
    private final boolean inStock;

    private Search(SearchBuilder searchBuilder) {
        this.name = searchBuilder.name;
        this.color = searchBuilder.color;
        this.category = searchBuilder.category;
        this.lowerPrice = searchBuilder.lowerPrice;
        this.higherPrice = searchBuilder.higherPrice;
        this.lowerDate = searchBuilder.lowerExpiringDate;
        this.higherDate = searchBuilder.higherExpiringDate;
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
        return lowerDate;
    }

    public Date getHigherDate() {
        return higherDate;
    }

    public boolean getInStock() {
        return inStock;
    }

    boolean hasName(String name) {
        return name != null;
    }

    boolean hasColor(String color) {
        return color != null;
    }

    boolean hasCategory(String category) {
        return category != null;
    }

    boolean hasLowerPrice(double lowerPrice) {
        return lowerPrice > 0;
    }

    boolean hasHigherPrice(double higherPrice) {
        return higherPrice > 0;
    }

    boolean hasLowerDate(Date lowerDate){
        return lowerDate != null;
    }

    boolean hasHgigherDate(Date higherDate){
        return higherDate != null;
    }

    boolean isInStock(boolean inStock){
        return inStock = true;
    }

    public static class SearchBuilder {

        String name, color, category;
        double lowerPrice, higherPrice;
        Date lowerExpiringDate, higherExpiringDate;
        boolean inStock;

        public SearchBuilder name(String name) {
            if(name != null){
                this.name = name;
            }            
            return this;
        }

        public SearchBuilder color(String color) {
            if(color != null){
                this.color = color;
            }            
            return this;
        }

        public SearchBuilder category(String category) {
            if(category != null){
                this.category = category;
            }            
            return this;
        }

        public SearchBuilder lowerPrice(Double lowerPrice) {
            if(lowerPrice != null){
                this.lowerPrice = lowerPrice;
            }
            return null;
        }

        public SearchBuilder higherPrice(Double higherPrice) {
            if(higherPrice != null){
                this.higherPrice = higherPrice;
            }            
            return null;
        }

        public SearchBuilder lowerExpiringDate(Date lowerExpiringDate) {
            if(lowerExpiringDate != null){
                this.lowerExpiringDate = lowerExpiringDate;
            }            
            return this;
        }

        public SearchBuilder higherExpiringDate(Date higherExpiringDate) {
            if(higherExpiringDate != null){
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