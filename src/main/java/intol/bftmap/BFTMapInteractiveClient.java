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
        System.out.println("\tMINT: Mint a new coin");
        System.out.println("\tSPEND: Spend coins");
        System.out.println("\tMY_COINS: Retrieve owned coins");
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
            } else if (cmd.equalsIgnoreCase("EXIT")) {
                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);
            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }
}
