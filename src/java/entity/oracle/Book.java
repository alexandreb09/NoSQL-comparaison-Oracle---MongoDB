package entity.oracle;

import main.Parameters;
import main.Utils;
import oracle.kv.Key;

import java.util.*;

public class Book {
    public static final String BOOK = "BOOK";

    private String title;
    private String price;
    private ArrayList<Author> authors;
    // TODO: a book might have several author

    public Book(){
        this.authors = new ArrayList<>();
    }
    public Book(String title_, String price_) {
        this.title = title_;
        this.price = price_;
        this.authors = new ArrayList<>();
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
    public String serialize(){
        return title + "¤" + price + "¤" + serializeAuthors();
    }

    /**
     * Partial serialisation ignoring author field
     * @return String: serialized Book class
     */
    public String serializePartial(){
        return title + "¤" + price;
    }

    /**
     * Serialize the list of authors (only their lastname, firstname and location)
     *      -> Books are ignored to avoid infinite recursion
     * @return String: serialized authors
     */
    public String serializeAuthors(){
        StringBuilder str = new StringBuilder();
        for (Author author: authors) {
            str.append(author.serializePartial()).append("¤");
        }
        if (authors.size() > 0){
            return str.toString().substring(0, str.length() - 1);
        }
        return str.toString();
    }


    public void deserialize(String str_book){
        List<String> parts = Arrays.asList(str_book.split("¤"));
        if (parts.size() >= 2){
            this.setTitle(parts.get(0));
            this.setprice(parts.get(1));

            if (parts.size() > 2 && (parts.size() - 2) % 3 == 0){
                for (int i = 2; i < parts.size(); i = i + 3){
                    Author author = new Author(parts.get(i),parts.get(i+1), parts.get(i+2));
                    this.addAuthor(author);
                }
            }
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
        String title = "title_" + System.currentTimeMillis() + "_";
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
    public static Book createRandomBook(int i){
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

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> author) {
        this.authors = author;
    }

    public void addAuthor(Author author){
        authors.add(author);
    }

    public void removeAuthor(Author author){
        authors.remove(author);
    }
}
