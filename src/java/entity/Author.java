package entity;

public abstract class Author {
    public static final String AUTHOR = "AUTHOR";

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    protected String lastName;
    protected String firstName;
    protected String location;

    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public Author(){}

    public Author(String lastName_, String firstName_, String location_) {
        this.lastName = lastName_;
        this.firstName = firstName_;
        this.location = location_;
    }

    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    @Override
    public String toString() {
        return "Author{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", location='" + location + '\'' +
                '}';
    }


    /* ************************************ */
    /*          GETTER - SETTER             */
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
}
