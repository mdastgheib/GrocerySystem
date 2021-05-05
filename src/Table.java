public class Table
{
    public Table(String location, String expiration, String item) {
        this.location = location;
        this.expiration = expiration;
        this.item = item;
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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    int quantity;
    String location, expiration, item;
    


}
