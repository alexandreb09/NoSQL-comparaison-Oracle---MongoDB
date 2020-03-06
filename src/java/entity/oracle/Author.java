package entity.oracle;


import oracle.kv.Key;
import oracle.kv.Value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Author {
    public static final String AUTHOR = "AUTHOR";

    private String lastName;
    private String firstName;
    private String location;

    private ArrayList<Book> books;

    public Author(){
        books = new ArrayList<>();
    }

    public Author(String lastName_, String firstName_, String location_) {
        this.lastName = lastName_;
        this.firstName = firstName_;
        this.location = location_;

        books = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Author{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String afficher(){
        String txt = "Author: "
                + "\n\t- LASTNAME: " + getlastName()
                + "\n\t- FIRSTNAME: " + getfirstName()
                + "\n\t- location: " + getlocation();
        System.out.println(txt);
        return txt;
    }

    public String serialize(){
        return lastName + "¤" + firstName + "¤" + location + "¤" + serializeBooks();
    }

    /**
     * Partial serialisation ignoring author field
     * @return String: serialized Book class
     */
    public String serializePartial(){
        return lastName + "¤" + firstName + "¤" + location;
    }

    public String serializeBooks(){
        StringBuilder str = new StringBuilder();
        for (Book book: books) {
            str.append(book.serializePartial()).append("¤");
        }
        if (books.size() > 0){
            return str.toString().substring(0, str.length() - 1);
        }
        return str.toString();
    }


    public Author deserialize(String str_data){
        List<String> parts = Arrays.asList(str_data.split("¤"));
        boolean success = parts.size() >= 3;
        if (success){
            this.setlastName(parts.get(0));
            this.setfirstName(parts.get(1));
            this.setlocation(parts.get(2));

            if (parts.size() > 3 && (parts.size() - 3) % 2 == 0){
                for (int i = 3; i < parts.size(); i = i + 2){
                    Book book = new Book(parts.get(i),parts.get(i+1));
                    this.addBook(book);
                }
            }
        }
        return this;
    }

    public Key getKey(){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(AUTHOR);
        tab_key.add(this.getlastName());
        tab_key.add(this.getfirstName());
        return Key.createKey(tab_key);
    }


    /**
     * Create an Author from a given integer
     * @param i: Author number
     * @return Author
     */
    public static Author randomAuthor(int i){
        return new Author("Author_" + i, "firstName_" + i, "location_" + i);
    }

    /* ************************************ */
    /* Getter - setter                      */
    /* ************************************ */
    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public void addBook(Book book_){
        books.add(book_);
    }

    public void removeBook(Book book_){
        books.remove(book_);
    }
}
