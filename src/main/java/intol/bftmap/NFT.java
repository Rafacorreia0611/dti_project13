package intol.bftmap;

import java.io.Serializable;

public class NFT implements Serializable {
    private int id;
    private int owner;
    private String name;
    private String uri;
    private float value;

    public NFT(int id, int owner, String name, String uri, float value) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.uri = uri;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
