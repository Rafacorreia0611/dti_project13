package intol.bftmap;

import java.io.Console;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap bftMap = new BFTMap(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("Coin commands:");
        System.out.println("\tMINT: Mint a new coin");
        System.out.println("\tSPEND: Spend coins");
        System.out.println("\tMY_COINS: Retrieve owned coins");
        System.out.println("NFT commands:");
        System.out.println("\tMY_NFTS: Retrieve owned NFTs");
        System.out.println("\tMINT_NFT: Mint a new NFT");
        System.out.println("\tSET_NFT_PRICE: Set the price of an NFT");
        System.out.println("\tSEARCH_NFT: Search for an NFT");
        System.out.println("\tBUY_NFT: Buy an NFT\n");
        System.out.println("\tEXIT: Terminate this client\n");


        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("MINT")) {
                float value;

                try {
                    value = Float.parseFloat(console.readLine("Enter coin value: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid value!\n");
                    continue;
                }

                int coinId = bftMap.mint(value);

                if (coinId > 0) {
                    System.out.println("\nMinted coin with ID: " + coinId + "\n");
                } else {
                    System.out.println("\nFailed to mint coin\n");
                }
            } else if (cmd.equalsIgnoreCase("SPEND")) {
                LinkedList<Integer> coinIds = new LinkedList<>();
                String[] coins = console.readLine("Enter coin IDs (comma-separated): ").split(",");

                for (String coin : coins) {
                    try {
                        coinIds.add(Integer.parseInt(coin.trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("\tInvalid coin ID!\n");
                        continue;
                    }
                }

                int receiver;

                try {
                    receiver = Integer.parseInt(console.readLine("Enter receiver ID: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid receiver ID!\n");
                    continue;
                }

                float value;

                try {
                    value = Float.parseFloat(console.readLine("Enter amount to spend: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid value!\n");
                    continue;
                }

                boolean success = bftMap.spend(coinIds, receiver, value);
                
                if (success) {
                    System.out.println("\nTransaction successful\n");
                } else {
                    System.out.println("\nTransaction failed\n");
                }
            } else if (cmd.equalsIgnoreCase("MY_COINS")) {
                LinkedList<Coin> coins = bftMap.myCoins();

                System.out.println("\nOwned coins: \n");

                for (Coin coin : coins) {
                    System.out.println("Coin id: " + coin.getId() + "; value: " + coin.getValue() + "\n");
                }

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {
                LinkedList<NFT> nfts = bftMap.myNFTs();

                if (!nfts.isEmpty()) {
                    System.out.println("\nOwned NFTs: \n");

                    for (NFT nft : nfts) {
                        System.out.println("NFT id: " + nft.getId() + "; name: " + nft.getName() + "; value: " + nft.getValue() + "\n");
                    }
                } else {
                    System.out.println("\nNo owned NFTs\n");
                }

            } else if (cmd.equalsIgnoreCase("MINT_NFT")) {
                
                String name = console.readLine("Enter NFT name: ");
                String uri = console.readLine("Enter NFT URI: ");
                float value = Float.parseFloat(console.readLine("Enter NFT value: "));

                int nftId = bftMap.mintNFT(name, uri, value);

                if (nftId > 0) {
                    System.out.println("\nMinted NFT with ID: " + nftId + "\n");
                } else {
                    System.out.println("\nFailed to mint NFT\n");
                }

            } else if (cmd.equalsIgnoreCase("SET_NFT_PRICE")) {
                int nftId = Integer.parseInt(console.readLine("Enter NFT ID: "));
                float value = Float.parseFloat(console.readLine("Enter new NFT value: "));

                boolean success = bftMap.set_nft_price(nftId, value);

                if (success) {
                    System.out.println("\nNFT price updated to: " + value + "\n");
                } else {
                    System.out.println("\nFailed to update NFT price to: " + value + "\n");
                }

            } else if (cmd.equalsIgnoreCase("SEARCH_NFT")) {
                String text = console.readLine("Enter search text: ");

                LinkedList<NFT> nfts = bftMap.search_nft(text);

                if (!nfts.isEmpty()) {
                    System.out.println("\nFound NFTs: \n");

                    for (NFT nft : nfts) {
                        System.out.println("NFT id: " + nft.getId() + "; name: " + nft.getName() + "; value: " + nft.getValue() + "\n");
                    }
                } else {
                    System.out.println("\nNo NFTs found\n");
                }

            } else if (cmd.equalsIgnoreCase("BUY_NFT")) {
                int nftId = Integer.parseInt(console.readLine("Enter NFT ID: "));
                String[] coinIds = console.readLine("Enter coin IDs (comma-separated): ").split(",");

                LinkedList<Integer> coins = new LinkedList<>();
                
                for (String coin : coinIds) {
                    coins.add(Integer.parseInt(coin.trim()));
                }

                int coinId = bftMap.buy_nft(nftId, coins);

                if (coinId > 0) {
                    System.out.println("\nTransaction successful\n");
                    System.out.println("\nRemaining value saved in coin with ID: " + coinId + "\n");
                }else if(coinId == 0){
                    System.out.println("\nTransaction successful\n");
                    System.out.println("\nNo remaining value\n");
                } else {
                    System.out.println("\nTransaction failed\n");
                }

            } else if (cmd.equalsIgnoreCase("EXIT")) {
                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);
            
            } else {
                System.out.println("\tInvalid command :P\n");
            }
            
        }
    }
}
