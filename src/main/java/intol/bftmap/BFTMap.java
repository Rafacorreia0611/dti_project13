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

    public LinkedList<NFT> myNFTs() {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.MY_NFTS);
            
            // Invokes BFT-SMaRt unordered request
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MY_NFTS request", e);

            return new LinkedList<>();
        }

        if (rep.length == 0) {
            return new LinkedList<>();
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getNfts();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of MY_NFTS request", ex);

            return new LinkedList<>();
        }
    }

    public int mintNFT(String name, String uri, float value) {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.MINT_NFT);
            request.setNftName(name);
            request.setNftUri(uri);
            request.setNftValue(value);
            
            // Invokes BFT-SMaRt ordered request
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MINT_NFT request", e);

            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getNftId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of MINT_NFT request", ex);

            return -1;
        }
    }

    public boolean set_nft_price(int nftId, float value) {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.SET_NFT_PRICE);
            request.setNftId(nftId);
            request.setNftValue(value);
            
            // Invokes BFT-SMaRt ordered request
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SET_NFT_PRICE request", e);

            return false;
        }

        if (rep.length == 0) {
            return false;
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.isSuccess();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of SET_NFT_PRICE request", ex);

            return false;
        }
    }

    public LinkedList<NFT> search_nft(String text) {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.SEARCH_NFT);
            request.setText(text);
            
            // Invokes BFT-SMaRt unordered request
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SEARCH_NFT request", e);

            return new LinkedList<>();
        }

        if (rep.length == 0) {
            return new LinkedList<>();
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getNfts();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of SEARCH_NFT request", ex);

            return new LinkedList<>();
        }
    }

    public int buy_nft(int nftid, LinkedList<Integer> coinsIds) {
        byte[] rep;

        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.BUY_NFT);
            request.setNftId(nftid);
            request.setCoinIds(coinsIds);
            
            // Invokes BFT-SMaRt ordered request
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send BUY_NFT request", e);

            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);

            return response.getCoinId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of BUY_NFT request", ex);

            return -1;
        }
    }
}
