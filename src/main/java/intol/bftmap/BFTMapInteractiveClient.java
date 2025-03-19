package intol.bftmap;

import java.io.Console;
import java.io.IOException;
import java.util.LinkedList;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap bftMap = new BFTMap(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("Coin commands:");
        System.out.println("\tMY_COINS(mc): Retrieve owned coins\n");
        System.out.println("\tMINT(m): Mint a new coin");
        System.out.println("\tSPEND(s): Spend coins");
        System.out.println("NFT commands:");
        System.out.println("\tMY_NFTS(myn): Retrieve owned NFTs\n");
        System.out.println("\tMINT_NFT(mtn): Mint a new NFT");
        System.out.println("\tSET_NFT_PRICE(snp): Set the price of an NFT");
        System.out.println("\tSEARCH_NFT(sn): Search for NFTs");
        System.out.println("\tBUY_NFT(bn): Buy an NFT");
        System.out.println("Common commands:");
        System.out.println("\tHELP(h): Show this help message");
        System.out.println("\tEXIT: Terminate this client\n");


        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("MINT") || cmd.equalsIgnoreCase("M")) {
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
            } else if (cmd.equalsIgnoreCase("SPEND") || cmd.equalsIgnoreCase("S")) {
                LinkedList<Integer> coinIds = new LinkedList<>();
                String[] coins = console.readLine("Enter coin IDs (comma-separated): ").split(",");

                boolean invalidInput = false;
                for (String coin : coins) {
                    try {
                        coinIds.add(Integer.parseInt(coin.trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("\tInvalid coin ID!\n");
                        invalidInput = true;
                        break;
                    }
                }

                if (invalidInput) {
                    continue;
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

                int id = bftMap.spend(coinIds, receiver, value);
                
                if (id > 0) {
                    System.out.println("\nTransaction successful\n");
                    System.out.println("\nRemaining value saved in coin with ID: " + id + "\n");
                } else if (id == 0) {
                    System.out.println("\nTransaction successful\n");
                    System.out.println("\nNo remaining value\n");
                } else {
                    System.out.println("\nTransaction failed\n");
                    
                }
            } else if (cmd.equalsIgnoreCase("MY_COINS") || cmd.equalsIgnoreCase("MC")) {
                LinkedList<Coin> coins = bftMap.myCoins();

                if(coins.isEmpty()){
                    System.out.println("\nNo owned coins\n");
                    continue;
                }else{
                    System.out.println("\nOwned coins: \n");

                    for (Coin coin : coins) {
                        System.out.println("Coin id: " + coin.getId() + "; value: " + coin.getValue() + "\n");
                    }
                }
                
            } else if (cmd.equalsIgnoreCase("MY_NFTS") || cmd.equalsIgnoreCase("MYN")) {
                LinkedList<NFT> nfts = bftMap.myNFTs();

                if (!nfts.isEmpty()) {
                    System.out.println("\nOwned NFTs: \n");

                    for (NFT nft : nfts) {
                        System.out.println("NFT id: " + nft.getId() + "; name: " + nft.getName() + "; uri: " + nft.getUri() + "; value: " + nft.getValue() + "\n");
                    }
                } else {
                    System.out.println("\nNo owned NFTs\n");
                }

            } else if (cmd.equalsIgnoreCase("MINT_NFT") || cmd.equalsIgnoreCase("MTN")) {
                
                String name = console.readLine("Enter NFT name: ");
                if(name.isEmpty()){
                    System.out.println("\nInvalid name\n");
                    continue;
                }else if(name.length() > 255){
                    System.out.println("\nName too long\n");
                    continue;
                }
                String uri = console.readLine("Enter NFT URI: ");
                if(uri.isEmpty()){
                    System.out.println("\nInvalid URI\n");
                    continue;
                }else if(uri.length() > 255){
                    System.out.println("\nURI too long\n");
                    continue;
                }
                float value;
                try {
                    value = Float.parseFloat(console.readLine("Enter NFT value: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid value!\n");
                    continue;
                }
                if(value <= 0){
                    System.out.println("\nInvalid value\n");
                    continue;
                }else if(value > Float.MAX_VALUE){
                    System.out.println("\nValue too high\n");
                    continue;
                }

                int nftId = bftMap.mintNFT(name, uri, value);

                if (nftId > 0) {
                    System.out.println("\nMinted NFT with ID: " + nftId + "\n");
                } else {
                    System.out.println("\nFailed to mint NFT, name already exists\n");
                }

            } else if (cmd.equalsIgnoreCase("SET_NFT_PRICE") || cmd.equalsIgnoreCase("SNP")) {

                int nftId;
                try {
                    nftId = Integer.parseInt(console.readLine("Enter NFT ID: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid NFT ID!\n");
                    continue;
                }
                float value;
                try {
                    value = Float.parseFloat(console.readLine("Enter NFT price: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid value!\n");
                    continue;
                }
                if (value <= 0) {
                    System.out.println("\nInvalid value\n");
                    continue;
                } else if (value > Float.MAX_VALUE) {
                    System.out.println("\nValue too high\n");
                    continue;
                }

                boolean success = bftMap.set_nft_price(nftId, value);

                if (success) {
                    System.out.println("\nNFT price updated to: " + value + "\n");
                } else {
                    System.out.println("\nFailed to update NFT price to: " + value + "\n");
                }

            } else if (cmd.equalsIgnoreCase("SEARCH_NFT") || cmd.equalsIgnoreCase("SN")) {
                String text = console.readLine("Enter search text: ");
                if(text.isEmpty()){
                    System.out.println("\nInvalid search text\n");
                    continue;
                }else if(text.length() > 255){
                    System.out.println("\nSearch text too long\n");
                    continue;
                }

                LinkedList<NFT> nfts = bftMap.search_nft(text);

                if (!nfts.isEmpty()) {
                    System.out.println("\nFound NFTs: \n");

                    for (NFT nft : nfts) {
                        System.out.println("NFT id: " + nft.getId() + "; name: " + nft.getName() + "; uri: " + nft.getUri() + "; value: " + nft.getValue() + "\n");
                    }
                } else {
                    System.out.println("\nNo NFTs found\n");
                }

            } else if (cmd.equalsIgnoreCase("BUY_NFT") || cmd.equalsIgnoreCase("BN")) {
                int nftId;
                try {
                    nftId = Integer.parseInt(console.readLine("Enter NFT ID: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tInvalid NFT ID!\n");
                    continue;
                }
                String[] coinIds = console.readLine("Enter coin IDs (comma-separated): ").split(",");

                LinkedList<Integer> coins = new LinkedList<>();
                
                for (String coin : coinIds) {
                    try {
                        coins.add(Integer.parseInt(coin.trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("\tInvalid coin ID!\n");
                        continue;
                    }
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

            } else if (cmd.equalsIgnoreCase("HELP") || cmd.equalsIgnoreCase("H")) {
                System.out.println("\nCommands:\n");
                System.out.println("Coin commands:");
                System.out.println("\tMY_COINS(mc): Retrieve owned coins\n");
                System.out.println("\tMINT(m): Mint a new coin");
                System.out.println("\tSPEND(s): Spend coins");
                System.out.println("NFT commands:");
                System.out.println("\tMY_NFTS(myn): Retrieve owned NFTs\n");
                System.out.println("\tMINT_NFT(mtn): Mint a new NFT");
                System.out.println("\tSET_NFT_PRICE(snp): Set the price of an NFT");
                System.out.println("\tSEARCH_NFT(sn): Search for NFTs");
                System.out.println("\tBUY_NFT(bn): Buy an NFT");
                System.out.println("Common commands:");
                System.out.println("\tHELP(h): Show this help message");
                System.out.println("\tEXIT: Terminate this client\n");

            } else if (cmd.equalsIgnoreCase("EXIT")) {
                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);
            
            } else {
                System.out.println("\tInvalid command :P\n");
            }
            
        }
    }
}
