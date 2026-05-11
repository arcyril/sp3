package entities;

public abstract class Pracovnik {
    public int id;
    public String lokacia;
    public OSPAnimator.AnimItem animaciaPracovnika;

    public Pracovnik(int id, String lokacia) {
        this.id = id;
        this.lokacia = lokacia;
    }
}
