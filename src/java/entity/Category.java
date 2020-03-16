package entity;

import main.Parameters;

import java.util.Random;

public abstract class Category {
    public static final String CATEGORY = "CATEGORY";

    private String category;

    /* ************************************ */
    /*             Static methods           */
    /* ************************************ */
    /**
     * Randomly select a category from the category list in parameters
     * @return category (String)
     */
    public static String getRandomCategory(){
        return Parameters.BOOKS_CATEGORY.get(new Random().nextInt(Parameters.BOOKS_CATEGORY.size()));
    }


    /* ************************************ */
    /*            Getter - setter           */
    /* ************************************ */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
