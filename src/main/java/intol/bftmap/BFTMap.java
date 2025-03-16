package intol.bftmap;

import java.io.IOException;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bftsmart.tom.ServiceProxy;

public class BFTMap {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceProxy serviceProxy;

    public BFTMap(int id) {
        serviceProxy = new ServiceProxy(id);
    }

    public LinkedList<Coin> myCoins() {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage(BFTMapRequestType.MY_COINS);
            
            // Invokes BFT-SMaRt unordered request
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MY_COINS request", e);

            return new LinkedList<>();
        }

        if (rep.length == 0) {
            return new LinkedList<>();
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getCoins();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of MY_COINS request", ex);

            return new LinkedList<>();
        }
    }
    
    public int mint(float value) {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage(BFTMapRequestType.MINT, value);
            
            // Invokes BFT-SMaRt ordered request
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MINT request", e);

            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getCoinId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of MINT request", ex);

            return -1;
        }
    }
    
    public boolean spend(LinkedList<Integer> coinIds, int receiver, float value) {
        byte[] rep;
        
        try {
            BFTMapMessage request = new BFTMapMessage(BFTMapRequestType.SPEND, coinIds, receiver, value);
            
            // Invokes BFT-SMaRt ordered request
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SPEND request", e);

            return false;
        }

        if (rep.length == 0) {
            return false;
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getCoinId() > 0;
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of SPEND request", ex);

            return false;
        }
    }
}
