// Ethan Smith, Luis Pereda, Celestino Moreno, Alexander Ruvalcaba
// 09/10/2024
// Added betting system, adds ace logic (if total>21 then ace=1), has replay ability

package blackJackOOD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BlackJack {

    // Card deck
    private static Card[] cards = new Card[52];
    private static int currentCardIndex = 0;

    // Variables to track wins, losses, and ties
    private static int wins = 0;
    private static int losses = 0;
    private static int ties = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuePlaying = true;

        while (continuePlaying) {
            // Check if player balance is zero
            if (Card.getPlayerBalance() <= 0) {
                System.out.println("Your balance is zero. Game over!");
                break;
            }

            System.out.println("Your current balance is: $" + Card.getPlayerBalance());
            System.out.print("Enter your bet: $");
            int betAmount = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Check if bet amount is valid
            if (betAmount <= 0) {
                System.out.println("Bet amount must be greater than zero.");
                continue;
            }

            try {
                Card.placeBet(betAmount);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue; // Skip to the next game if bet is invalid
            }

            initializeDeck();
            shuffleDeck();

            List<Card> playerCards = new ArrayList<>();
            List<Card> dealerCards = new ArrayList<>();

            // Deal initial cards
            playerCards.add(dealCard());
            playerCards.add(dealCard());
            dealerCards.add(dealCard());
            dealerCards.add(dealCard());

            System.out.println("Your cards: " + playerCards.get(0) + " and " + playerCards.get(1));
            System.out.println("Dealer's cards: " + dealerCards.get(0) + " and [hidden]");

            int playerTotal = calculateHandValue(playerCards);
            int dealerTotal = dealerCards.get(0).getValue() + dealerCards.get(1).getValue(); // Dealer's total is not fully revealed yet

            // Player's turn
            playerTotal = playerTurn(scanner, playerCards);
            if (playerTotal > 21) {
                // Player busts
                System.out.println("You busted! Dealer wins.");
                Card.settleBet(false, false); // Player loses
                losses++;
                // No need to continue to dealer's turn
               continue; // Continue to the next game
            }

            // Dealer's turn
            dealerTotal = dealerTurn(dealerCards);
            boolean playerWins = determineWinner(playerTotal, dealerTotal);

            // Settle the bet based on the result
            if (playerWins) {
                Card.settleBet(true, false); // Player wins
                wins++;
            } else if (dealerTotal == playerTotal) {
                Card.settleBet(false, true); // It's a tie
                ties++;
            } else {
                Card.settleBet(false, false); // Player loses
                losses++;
            }

            // Display game statistics
            System.out.println("Wins: " + wins + ", Losses: " + losses + ", Ties: " + ties);

            // Ask the player if they want to play again
            System.out.println("Would you like to play another hand? (yes/no)");
            String playerDecision = scanner.nextLine().toLowerCase();

            while (!(playerDecision.equals("no") || playerDecision.equals("yes"))) {
                System.out.println("Invalid action. Please type 'yes' or 'no'.");
                playerDecision = scanner.nextLine().toLowerCase();
            }

            if (playerDecision.equals("no")) {
                continuePlaying = false;
            }
        }
        System.out.println("Thanks for playing!");
    }

    // Initialize the deck of cards
    private static void initializeDeck() {
        String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        int suitIndex = 0, rankIndex = 0;
        for (int i = 0; i < cards.length; i++) {
            int value;
            switch (RANKS[rankIndex]) {
                case "Jack":
                case "Queen":
                case "King":
                    value = 10;
                    break;
                case "Ace":
                    value = 11; // Aces can be worth 1 or 11
                    break;
                default:
                    value = Integer.parseInt(RANKS[rankIndex]);
                    break;
            }
            cards[i] = new Card(value, SUITS[suitIndex], RANKS[rankIndex]);
            suitIndex++;
            if (suitIndex == 4) {
                suitIndex = 0;
                rankIndex++;
            }
        }
    }

    // Shuffle the deck of cards
    private static void shuffleDeck() {
        Random random = new Random();
        for (int i = 0; i < cards.length; i++) {
            int index = random.nextInt(cards.length);
            Card temp = cards[i];
            cards[i] = cards[index];
            cards[index] = temp;
        }
        currentCardIndex = 0; // Reset card index after shuffling
    }

    // Deal a card from the deck
    private static Card dealCard() {
        if (currentCardIndex >= cards.length) {
            throw new IllegalStateException("No more cards in the deck");
        }
        return cards[currentCardIndex++];
    }

    // Handle the player's turn
    private static int playerTurn(Scanner scanner, List<Card> playerCards) {
        int playerTotal = calculateHandValue(playerCards);
        while (true) {
            System.out.println("Your total is " + playerTotal + ". Do you want to hit or stand?");
            String action = scanner.nextLine().toLowerCase();
            if (action.equals("hit")) {
                Card newCard = dealCard();
                playerCards.add(newCard);
                playerTotal = calculateHandValue(playerCards);
                System.out.println("You drew a " + newCard);
                if (playerTotal > 21) {
                    return playerTotal; // Player busts, return the bust value to handle the end of game
                }
            } else if (action.equals("stand")) {
                break;
            } else {
                System.out.println("Invalid action. Please type 'hit' or 'stand'.");
            }
        }
        return playerTotal;
    }

    // Handle the dealer's turn
    private static int dealerTurn(List<Card> dealerCards) {
        int dealerTotal = calculateHandValue(dealerCards);
        System.out.println("Dealer's total is " + dealerTotal);
        while (dealerTotal < 17) { // Dealer hits if total is less than 17
            Card newCard = dealCard();
            dealerCards.add(newCard);
            dealerTotal = calculateHandValue(dealerCards);
            System.out.println("Dealer drew a " + newCard);
        }
        System.out.println("Dealer's final total is " + dealerTotal);
        return dealerTotal;
    }

    // Calculate hand value while adjusting for Aces
    private static int calculateHandValue(List<Card> cards) {
        int totalValue = 0;
        int aceCount = 0;

        for (Card card : cards) {
            int value = card.getValue();
            if (card.getRank().equals("Ace")) {
                aceCount++;
                totalValue += 11; // Initially count Aces as 11
            } else {
                totalValue += value;
            }
        }

        // Adjust for Aces if total value is over 21
        while (totalValue > 21 && aceCount > 0) {
            totalValue -= 10;
            aceCount--;
        }

        return totalValue;
    }

    // Determine the winner of the game
    private static boolean determineWinner(int playerTotal, int dealerTotal) {
        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            System.out.println("You win!");
            return true; // Player wins
        } else if (dealerTotal == playerTotal) {
            System.out.println("It's a tie!");
            return false; // It's a tie, bet is returned
        } else {
            System.out.println("Dealer wins!");
            return false; // Player loses
        }
    }
}
