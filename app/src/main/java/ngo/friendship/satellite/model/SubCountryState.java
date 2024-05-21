package ngo.friendship.satellite.model;

public class SubCountryState {

    public static int CITY_TYPE = 0;
    public static int COUNTRY_TYPE = 1;

    private String name;
    private String id;
    private int Type;


    public SubCountryState(String name, String description) {
        this.name = name;
        this.id = description;
    }
    public SubCountryState() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return id;
    }

    public void setDescription(String description) {
        this.id = description;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        this.Type = type;
    }
}