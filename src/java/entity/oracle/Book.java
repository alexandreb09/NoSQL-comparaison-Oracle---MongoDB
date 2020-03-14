package entity.oracle;

import main.Parameters;
import main.Utils;
import oracle.kv.Key;

import java.util.*;

public class Book {
    public static final String BOOK = "BOOK";

    private String title;
    private String price;
    private String category;
    private ArrayList<Author> authors;

    public Book(){
        this.authors = new ArrayList<>();
    }
    public Book(String title_, String price_, String category_) {
        this.title = title_;
        this.price = price_;
        this.category = category_;
        this.authors = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Livre{" +
                "title='" + title + "'" +
                ", price='" + price + "'" +
                ", category='" + category + "'" +
                '}';
    }

    /**
     * Assume there is only one author per book
     * In the serialization, only the firstName, lastName and location of the author are added
     *
     * @return String: serialized Book class
     */
    public String serialize(){
        return title + "¤" + price + "¤" + category + "¤" + serializeAuthors();
    }

    /**
     * Partial serialisation ignoring author field
     * @return String: serialized Book class
     */
    public String serializePartial(){
        return title + "¤" + price + "¤" + category;
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
        int n_fields_book = 3;
        int n_fields_author = 3;

        if (parts.size() >= n_fields_book){
            this.setTitle(parts.get(0));
            this.setPrice(parts.get(1));
            this.setCategory(parts.get(2));

            if (parts.size() > n_fields_book && (parts.size() - n_fields_book) % n_fields_author == 0){
                for (int i = n_fields_book; i < parts.size(); i = i + n_fields_author){
                    Author author = new Author(parts.get(i),parts.get(i+1), parts.get(i+2));
                    this.addAuthor(author);
                }
            }
        }
    }

    /**
     * Get a kvStore key
     * Key build following the hierarchy:
     *      - BOOK
     *      - Title letter
     *      - Price range
     *      - Price
     * @return Key: kvstore key
     */
    public Key getKey(){
        ArrayList<String> tab_key = new ArrayList<>();

        // Add book type
        tab_key.add(BOOK);

        // Add all important name as a key in th title
        tab_key.add(getTitleLetter(this.getTitle()));

        // compute price range
        tab_key.add(Integer.toString(priceRangeCompute(this)));

        // Add full price
        tab_key.add(this.getPrice());
        return Key.createKey(tab_key);
    }

    public Book afficher(){
        String txt = "Livre: "
                + "\n\t- TITLE: " + getTitle()
                + "\n\t- price: " + getPrice();
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
    /* STATIC method                       */
    /* ************************************ */
    /**
     * Create a random Livre from a given integer
     * @return Livre
     */
    public static Book createRandomBook(){
        Book book = new Book();
        book.setRandomTitle();
        book.setRandomprice();
        book.setCategory(Category.getRandomCategory());

        return book;
    }

    /**
     * Provide the last letter from the title
     * @param title: Book title (string)
     * @return letter: last title letter
     */
    public static String getTitleLetter(String title){
        String[] title_words = title.split("_");
        return title_words[title_words.length - 1];
    }

    public static int priceRangeCompute(Book book){
        return Integer.parseInt(book.getPrice()) / Parameters.BOOK_PRICE_RANGE;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory() {
        return this.category;
    }
}
