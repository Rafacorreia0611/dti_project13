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
     private LinkedList<NFT> nfts;
     private int nftId;
     private String nftName;
     private String nftUri;
     private float nftValue;
     private String text;
     private boolean success;

     public BFTMapMessage() {
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

     public LinkedList<NFT> getNfts() {
          return nfts;
     }

     public int getNftId() {
          return nftId;
     }

     public String getNftName() {
          return nftName;
     }

     public String getNftUri() {
          return nftUri;
     }

     public float getNftValue() {
          return nftValue;
     }

     public String getText() {
          return text;
     }

     public boolean isSuccess() {
          return success;
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

     public void setNfts(LinkedList<NFT> nfts) {
          this.nfts = nfts;
     }

     public void setNftId(int nftId) {
          this.nftId = nftId;
     }

     public void setNftName(String nftName) {
          this.nftName = nftName;
     }

     public void setNftUri(String nftUri) {
          this.nftUri = nftUri;
     }

     public void setNftValue(float nftValue) {
          this.nftValue = nftValue;
     }

     public void setText(String text) {
          this.text = text;
     }

     public void setSuccess(boolean success) {
          this.success = success;
     }
}
