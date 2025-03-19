package intol.bftmap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class BFTMapServer extends DefaultSingleRecoverable {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private TreeMap<Integer, Coin> coinLedger;
    private TreeMap<Integer, LinkedList<NFT>> nftLedger;
    private int nextNftId;
    private int nextCoinId;

    public BFTMapServer(int id) {
        coinLedger = new TreeMap<>();
        nextCoinId = 1;

        nftLedger = new TreeMap<>();
        nextNftId = 1;

        new ServiceReplica(id, this, this);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java BFTCoinServer <server id>");
            System.exit(-1);
        }

        new BFTMapServer(Integer.parseInt(args[0]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        try {
            BFTMapMessage request = BFTMapMessage.fromBytes(command);
            BFTMapMessage response = new BFTMapMessage(request.getType());
            int sender = msgCtx.getSender();
            BFTMapRequestType cmd = request.getType();

            logger.info("Ordered execution of a {} request from {}", cmd, sender);

            switch (cmd) {
                case MINT:
                    int newCoinId = nextCoinId++;
                    Coin newCoin = new Coin(newCoinId, sender, request.getValue());

                    coinLedger.put(newCoinId, newCoin);
                    response.setCoinId(newCoinId);

                    logger.info("Minted new coin {} for user {}", newCoinId, sender);

                    break;
                case SPEND:
                    LinkedList<Integer> coinIds = request.getCoinIds();
                    int receiver = request.getReceiver();
                    float value = request.getValue();
                    float total = 0;
                    LinkedList<Integer> validCoins = new LinkedList<>();

                    for (int coinId : coinIds) {
                        Coin coin = coinLedger.get(coinId);

                        if (coin != null && coin.getOwner() == sender) {
                            total += coin.getValue();
                            validCoins.add(coinId);
                        }
                    }

                    if (total >= value) {
                        for (int coinId : validCoins) {
                            coinLedger.remove(coinId);
                        }

                        int newCoinIdReceiver = nextCoinId++;
                        Coin newCoinReceiver = new Coin(newCoinIdReceiver, receiver, value);

                        coinLedger.put(newCoinIdReceiver, newCoinReceiver);

                        float remainingValue = total - value;
                        int newCoinIdSender = 0;

                        if (remainingValue > 0) {
                            newCoinIdSender = nextCoinId++;
                            Coin newCoinIssuer = new Coin(newCoinIdSender, sender, remainingValue);

                            coinLedger.put(newCoinIdSender, newCoinIssuer);
                        }

                        response.setCoinId(newCoinIdSender);

                        logger.info("User {} spent {} coins to user {}. Remaining coin: {}", sender, value, receiver, newCoinIdSender);
                    } else {
                        response.setCoinId(-1);
                        logger.warn("User {} tried to spend more coins than owned", sender);
                    }

                    break;

                case MY_NFTS:
                    LinkedList<NFT> ownedNFTs = new LinkedList<>();

                    if (nftLedger.containsKey(sender)) {
                        ownedNFTs = nftLedger.get(sender);
                    }

                    response.setNfts(ownedNFTs);

                    break;

                case MINT_NFT:
                    NFT newNft = new NFT(nextNftId, sender, request.getNftName(), request.getNftUri(), request.getNftValue());

                    for (LinkedList<NFT> nfts : nftLedger.values()) {
                        for (NFT nft : nfts) {
                            if (nft.getName().equals(newNft.getName())) {
                                response.setNftId(-1);
                                logger.warn("User {} tried to mint NFT with existing name {}", sender, newNft.getName());

                                return BFTMapMessage.toBytes(response);
                            }
                        }
                    }

                    if (!nftLedger.containsKey(sender)) {
                        nftLedger.put(sender, new LinkedList<>());
                    }

                    nftLedger.get(sender).add(newNft);
                    response.setNftId(nextNftId);                    

                    logger.info("Minted new NFT {} for user {}", nextNftId, sender);

                    nextNftId++;

                    break;

                case SET_NFT_PRICE:
                    int nftId = request.getNftId();
                    float nftPrice = request.getNftValue();

                    if (nftLedger.containsKey(sender)) {
                        LinkedList<NFT> owNfts = nftLedger.get(sender);

                        for (NFT nft : owNfts) {
                            if (nft.getId() == nftId) {
                                nft.setValue(nftPrice);
                                response.setSuccess(true);
                                logger.info("User {} set price of NFT {} to {}", sender, nftId, nftPrice);

                                break;
                            }
                        }
                    }
                    if(!response.isSuccess()){
                        response.setSuccess(false);
                        logger.warn("User {} tried to set price of non-existing NFT {}", sender, nftId);
                    }

                    break;
                case SEARCH_NFT:
                    String text = request.getText();
                    LinkedList<NFT> foundNFTs = new LinkedList<>();

                    for (LinkedList<NFT> nfts : nftLedger.values()) {
                        for (NFT nft : nfts) {
                            if (nft.getName().toLowerCase().contains(text)) {
                                foundNFTs.add(nft);
                            }
                        }
                    }

                    response.setNfts(foundNFTs);
                    break;

                case BUY_NFT:
                    int nftIdBuy = request.getNftId();
                    LinkedList<Integer> coinIdsBuy = request.getCoinIds();
                    float nftPriceBuy = 0;
                    int nftOwner = 0;
                    NFT nftBuy = null;

                    for (LinkedList<NFT> nfts : nftLedger.values()) {
                        for (NFT nft : nfts) {
                            if (nft.getId() == nftIdBuy) {
                                nftPriceBuy = nft.getValue();
                                nftOwner = nft.getOwner();
                                nftBuy = nft;
                                break;
                            }
                        }
                    }

                    if (nftPriceBuy > 0) {
                        float totalBuy = 0;
                        LinkedList<Integer> validCoinsBuy = new LinkedList<>();

                        for (int coinId : coinIdsBuy) {
                            Coin coin = coinLedger.get(coinId);

                            if (coin != null && coin.getOwner() == sender) {
                                totalBuy += coin.getValue();
                                validCoinsBuy.add(coinId);
                            }
                        }

                        if (totalBuy >= nftPriceBuy) {

                            nftBuy.setOwner(sender);
                            if (!nftLedger.containsKey(sender)) {
                                nftLedger.put(sender, new LinkedList<>());
                                
                            }
                            nftLedger.get(sender).add(nftBuy);
                            nftLedger.get(nftOwner).remove(nftBuy);

                            for (int coinId : validCoinsBuy) {
                                coinLedger.remove(coinId);
                            }

                            int newCoinIdReceiverBuy = nextCoinId++;
                            Coin newCoinReceiverBuy = new Coin(newCoinIdReceiverBuy, nftOwner, nftPriceBuy);

                            coinLedger.put(newCoinIdReceiverBuy, newCoinReceiverBuy);

                            float remainingValueBuy = totalBuy - nftPriceBuy;
                            int newCoinIdSenderBuy = 0;

                            if (remainingValueBuy > 0) {
                                newCoinIdSenderBuy = nextCoinId++;
                                Coin newCoinIssuerBuy = new Coin(newCoinIdSenderBuy, sender, remainingValueBuy);

                                coinLedger.put(newCoinIdSenderBuy, newCoinIssuerBuy);
                            }

                            response.setCoinId(newCoinIdSenderBuy);

                            logger.info("User {} bought NFT {} from user {}. Remaining coin: {}", sender, nftIdBuy, nftOwner, newCoinIdSenderBuy);
                        } else {
                            response.setCoinId(-1);
                            logger.warn("User {} tried to buy NFT {} without enough coins", sender, nftIdBuy);
                        }
                    } else {
                        response.setCoinId(-1);
                        logger.warn("User {} tried to buy non-existing NFT {}", sender, nftIdBuy);
                    }

                    break;
                default:
                    break;
            }

            return BFTMapMessage.toBytes(response);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to process ordered request", e);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        try {
            BFTMapMessage request = BFTMapMessage.fromBytes(command);
            BFTMapMessage response = new BFTMapMessage(request.getType());
            int sender = msgCtx.getSender();
            BFTMapRequestType cmd = request.getType();

            logger.info("Unordered execution of a {} request from {}", cmd, sender);

            switch (cmd) {
                case MY_COINS:
                    LinkedList<Coin> ownedCoins = new LinkedList<>();

                    for (Coin coin : coinLedger.values()) {
                        if (coin.getOwner() == sender) {
                            ownedCoins.add(coin);
                        }
                    }

                    response.setCoins(ownedCoins);

                    break;
                case MY_NFTS:
                    LinkedList<NFT> ownedNFTs = new LinkedList<>();

                    if (nftLedger.containsKey(sender)) {
                        ownedNFTs = nftLedger.get(sender);
                    }

                    response.setNfts(ownedNFTs);

                    break;

                case SEARCH_NFT:
                    String text = request.getText();
                    LinkedList<NFT> foundNFTs = new LinkedList<>();

                    for (LinkedList<NFT> nfts : nftLedger.values()) {
                        for (NFT nft : nfts) {
                            if (nft.getName().contains(text)) {
                                foundNFTs.add(nft);
                            }
                        }
                    }

                    response.setNfts(foundNFTs);
                    break;  
                default:
                    break;
            }

            return BFTMapMessage.toBytes(response);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to process unordered request", e);
            return new byte[0];
        }
    }

    @Override
    public byte[] getSnapshot() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(coinLedger);
            out.writeInt(nextCoinId);
            out.writeObject(nftLedger);
            out.writeInt(nextNftId);

            out.flush();
            bos.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(); //debug instruction

            return new byte[0];
        }
    }

    @Override
    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(state);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            coinLedger = (TreeMap<Integer, Coin>) in.readObject();
            nextCoinId = in.readInt();
            nftLedger = (TreeMap<Integer, LinkedList<NFT>>) in.readObject();
            nextNftId = in.readInt();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); //debug instruction
        }
    }
}
