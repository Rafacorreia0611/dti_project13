package intol.bftmap;

import java.io.Serializable;

public class Coin implements Serializable {
    private int id;
    private int owner;
    private float value;

    public Coin(int id, int owner, float value) {
        this.id = id;
        this.owner = owner;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public float getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
