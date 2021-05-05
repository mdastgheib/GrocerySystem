public class Table
{
    public Table(String item, String quantity, String price, String location, String expiration) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.location = location;
        this.expiration = expiration;

    }
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }







    String location, expiration, item, quantity, price;
    


}
