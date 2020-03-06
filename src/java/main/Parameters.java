package main;

public class Parameters {

    /* ******************************** */
    /*      CONFIG ORACLE               */
    /* ******************************** */
    public static final String STORE_NAME = "kvstore";
    public static final String HOST_NAME = "ALEX-PC";
    public static final String HOST_PORT = "5000";

    /* ******************************** */
    /*      CONFIG MONGODB              */
    /* ******************************** */


    /* ******************************** */
    /*      PARAMETERS                  */
    /* ******************************** */
    public static final int NB_AUTHOR_PER_THREAD    = 10;         // Nombre d'auteurs créés dans une thread
    public static final int NB_THREAD_CREATION      = 10;           // Nombre de thread dans la création des auteurs et des livres

    public static final int BOOK_PRICE_MIN          = 1;            // Min price
    public static final int BOOK_PRICE_MAX          = 100;          // Max price

    // Books number for one author
    // Ex. Author x has books: Book_x, Book_(x+BOOK_AUTHOR_MODULO), ...
    public static final int BOOK_AUTHOR_MODULO      = 50000;

    public static final int NUMBER_REQUEST          = 1000;

}
