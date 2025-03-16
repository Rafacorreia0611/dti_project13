package intol.bftmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

public class BFTMapMessage implements Serializable {
    private BFTMapRequestType type;
    private LinkedList<Coin> coins;
    private int coinId;
    private float value;
    private int receiver;
    private LinkedList<Integer> coinIds;

    public BFTMapMessage(BFTMapRequestType type) {
        this.type = type;
    }

    public BFTMapMessage(BFTMapRequestType type, float value) {
        this.type = type;
        this.value = value;
    }

    public BFTMapMessage(BFTMapRequestType type, LinkedList<Integer> coinIds, int receiver, float value) {
        this.type = type;
        this.coinIds = coinIds;
        this.receiver = receiver;
        this.value = value;
    }

    public static byte[] toBytes(BFTMapMessage message) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(message);

        objOut.flush();
        byteOut.flush();

        return byteOut.toByteArray();
    }

    public static BFTMapMessage fromBytes(byte[] rep) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        
        return (BFTMapMessage) objIn.readObject();
    }

    public BFTMapRequestType getType() {
        return type;
    }

    public LinkedList<Coin> getCoins() { 
        return coins; 
    }

    public int getCoinId() {
         return coinId; 
    }

    public float getValue() {
         return value; 
    }

    public int getReceiver() {
         return receiver; 
    }

    public LinkedList<Integer> getCoinIds() {
         return coinIds; 
    }

    public void setType(BFTMapRequestType type) {
         this.type = type; 
    }

    public void setCoins(LinkedList<Coin> coins) {
         this.coins = coins; 
    }

    public void setCoinId(int coinId) {
         this.coinId = coinId; 
    }

    public void setValue(float value) {
         this.value = value; 
    }

    public void setReceiver(int receiver) {
         this.receiver = receiver; 
    }

    public void setCoinIds(LinkedList<Integer> coinIds) {
         this.coinIds = coinIds; 
    }
}
