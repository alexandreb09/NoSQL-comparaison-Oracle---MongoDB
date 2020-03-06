package entity.oracle;

import main.Parameters;
import main.Utils;
import oracle.kv.Key;

import java.util.*;

public class Book {
    public static final String BOOK = "BOOK";

    private String title;
    private String price;
    private Author author;
    // TODO: a book might have several author

    public Book(){
        this.author = new Author();
    }
    public Book(String title_, String price_) {
        this.title = title_;
        this.price = price_;
        this.author = new Author();
    }

    @Override
    public String toString() {
        return "Livre{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    /**
     * Assume there is only one author per book
     * In the serialization, only the firstName, lastName and location of the author are added
     *
     * @return String: serialized Book class
     */
    // TODO: a book might have several author
    public String serialize(){
        return title + "¤" + price + "¤" + author.getfirstName() + "¤" + author.getlastName() + "¤" + author.getlocation();
    }

    /**
     * Partial serialisation ignoring author field
     * @return String: serialized Book class
     */
    public String serializePartial(){
        return title + "¤" + price;
    }

    // TODO: a book might have several author
    public void deserialize(String str_book){
        List<String> list_fields = Arrays.asList(str_book.split("¤"));
        if (list_fields.size() >= 2){
            this.setTitle(list_fields.get(0));
            this.setprice(list_fields.get(1));
        }
        if (5 == list_fields.size()){
            Author author = new Author(list_fields.get(2), list_fields.get(3), list_fields.get(4));
            this.setAuthor(author);
        }
    }

    public Key getKey(){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(BOOK);
        tab_key.add(this.getTitle());
        tab_key.add(this.getprice());
        return Key.createKey(tab_key);
    }

    public Book afficher(){
        String txt = "Livre: "
                + "\n\t- TITLE: " + getTitle()
                + "\n\t- price: " + getprice();
        System.out.println(txt);
        return this;
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
        String title = "title_";
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
    /* STATIC methode                       */
    /* ************************************ */
    /**
     * Create a random Livre from a given integer
     * @param i: livre number
     * @return Livre
     */
    public static Book createRandomLivre(int i){
        Book livre = new Book();
        livre.setRandomTitle();
        livre.setRandomprice();
        return livre;
    }

    /* ************************************ */
    /* Getter - setter                      */
    /* ************************************ */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getprice() {
        return price;
    }

    public void setprice(String price) {
        this.price = price;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
