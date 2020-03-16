package entity;

import main.Parameters;
import main.Utils;

import java.util.Random;
import java.util.UUID;

public abstract class Book {
    public static final String BOOK = "BOOK";

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    protected String title;
    protected String price;
    protected String category;


    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public Book(){}
    public Book(String title_, String price_, String category_) {
        this.title = title_;
        this.price = price_;
        this.category = category_;
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    @Override
    public String toString() {
        return "Livre{" +
                "title='" + title + "'" +
                ", price='" + price + "'" +
                ", category='" + category + "'" +
                '}';
    }

    /**
     * Generate a random price between the range defined in "Parameters" file
     */
    public void setRandomprice(){
        this.price = Integer.toString(Utils.getRandomInteger(Parameters.BOOK_PRICE_MIN, Parameters.BOOK_PRICE_MAX));
    }

    /**
     * Generate a random Title following the pattern "title_X" where X has:
     *      - 10% of being "A"
     *      - 30% of being "B"
     *      - 50% of being "C"
     */
    public void setRandomTitle(){
        String title = "title_" + UUID.randomUUID().toString().replace("-", "") + "_";
        float random = (new Random()).nextFloat();
        if (random < 0.1){
            title = title + "A";
        }else if (random < 0.4){
            title = title + "B";
        }else{
            title = title + "C";
        }
        this.setTitle(title);
    }


    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Provide the last letter from the title
     * @param title: Book title (string)
     * @return letter: last title letter
     */
    public static String getTitleLetter(String title){
        String[] title_words = title.split("_");
        return title_words[title_words.length - 1];
    }

    /**
     * Compute the price range (divide price by BOOK_PRICE_RANGE parameters)
     * @param book: book to apply price range computation
     * @return price range (int)
     */
    public static int priceRangeCompute(Book book){
        return priceRangeCompute(Math.round(Float.parseFloat(book.getPrice())));
    }

    /**
     * Compute the price range (divide price by BOOK_PRICE_RANGE parameters)
     * @param price: price to apply price range computation
     * @return price range (int)
     */
    public static int priceRangeCompute(int price){
        return price / Parameters.BOOK_PRICE_RANGE;
    }


    /* ************************************ */
    /*          GETTER - SETTER             */
    /* ************************************ */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price.replace(",", ".");
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }
}
