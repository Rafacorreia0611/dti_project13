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
    private int nextCoinId;

    public BFTMapServer(int id) {
        coinLedger = new TreeMap<>();
        nextCoinId = 1;

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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); //debug instruction
        }
    }
}
