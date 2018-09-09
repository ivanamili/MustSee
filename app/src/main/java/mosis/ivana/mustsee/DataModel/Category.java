package mosis.ivana.mustsee.DataModel;

public enum Category {
    History ("History"),
    Culture ("Culture"),
    Religion ("Religion"),
    MusicFestival ("Music festival"),
    FilmFestival ("Film festival"),
    FoodDrinkFestival ("Food/Drink festival"),
    OtherFestival("Other festival"),
    FoodDrink ("FoodDrink"),
    Accommodation("Accommodation"),
    Recreation("Recreation"),
    SportVenue ("Sport venue"),
    Nature ("Nature"),
    NightLife ("Night life"),
    Shopping ("Shopping"),
    Entertainment("Entertainment");

    private final String name;

    Category(String category) {
        name=category;
    }

    public  boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

   @Override public String toString() {
        return this.name;
    }
}
