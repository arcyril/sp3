package entities;

public abstract class Pracovnik {
    public int id;
    public String lokacia; //?? maybe switch to enum

    public Pracovnik(int id, String lokacia) {
        this.id = id;
        this.lokacia = lokacia;
    }
}
