package sample;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;

public class Person implements Serializable{
    // unique id
    private static final long serialVersionUID = 7526472295622776147L;

    public static int counter = 1;
    transient private SimpleIntegerProperty id;
    private int serId;
    transient private SimpleStringProperty name;
    private String serName;
    private int idDrawedPerson = 0;
    transient private SimpleStringProperty nameDrawedPerson;
    private String serNameDrawedPerson;
    private Person drawedPerson = null;

    public Person(String name) {
        this.id = new SimpleIntegerProperty(counter);
        this.serId = counter;
        this.name = new SimpleStringProperty(name);
        this.serName = name;
        this.nameDrawedPerson = new SimpleStringProperty("not known");
    }

    // I use it to reconstruct when loading from file and rebuilding person
    public Person(int id, String name, String nameDrawedPerson) {
        this.id = new SimpleIntegerProperty(id);
        this.serId = id;
        this.name = new SimpleStringProperty(name);
        this.serName = name;
        this.nameDrawedPerson = new SimpleStringProperty(nameDrawedPerson);
        this.serNameDrawedPerson = nameDrawedPerson;
    }


    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public String getSerName() {
        return serName;
    }

    public String getSerNameDrawedPerson() {
        return serNameDrawedPerson;
    }

    public void setSerNameDrawedPerson(String serNameDrawedPerson) {
        this.serNameDrawedPerson = serNameDrawedPerson;
    }

    public int getIdDrawedPerson() {
        return idDrawedPerson;
    }

    public void setIdDrawedPerson(int idDrawedPerson) {
        this.idDrawedPerson = idDrawedPerson;
    }

    public Person getDrawedPerson() {
        return drawedPerson;
    }

    public void setDrawedPerson(Person drawedPerson) {
        this.drawedPerson = drawedPerson;
        this.nameDrawedPerson.set(drawedPerson.getName());
        setSerNameDrawedPerson(drawedPerson.getName());
    }

    public SimpleStringProperty nameDrawedPersonProperty() {
        return nameDrawedPerson;
    }

    @Override
    public String toString() {
        return id + ". Person: " + name + " wylosowała" + nameDrawedPerson;
    }

    public String printPersonInfo(){
        return id + ". Person: " + this.getName() + " --> " + drawedPerson.getName();

    }public String printPersonInfoSer(){
        return String.format("%2d",serId) + ". Person: " + serName + " --> " + serNameDrawedPerson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return serId == person.serId &&
                Objects.equals(getSerName(), person.getSerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(serId, getSerName());
    }
}
