// Ethan Smith, Luis Pereda, Celestino Moreno, Alexander Ruvalcaba
// 09/10/2024
// Added Card class to get default code to run, created methods for betting system

package blackJackOOD;

public class Card {
    private int value; // The value of the card (e.g., 2 through 10, 10 for face cards, 11 for Ace)
    private String suit; // The suit of the card (e.g., Hearts, Diamonds, Clubs, Spades)
    private String rank; // The rank of the card (e.g., 2, 3, ..., 10, Jack, Queen, King, Ace)

    // Static variable to keep track of the player's balance
    private static int playerBalance = 1000; // Initial balance

    // Variable to store the current bet
    private static int currentBet = 0;

    // Constructor to initialize the card
    public Card(int value, String suit, String rank) {
        this.value = value;
        this.suit = suit;
        this.rank = rank;
    }

    // Accessor for the card's value
    public int getValue() {
        return value;
    }

    // Mutator for the card's value
    public void setValue(int value) {
        this.value = value;
    }

    // Accessor for the card's suit
    public String getSuit() {
        return suit;
    }

    // Mutator for the card's suit
    public void setSuit(String suit) {
        this.suit = suit;
    }

    // Accessor for the card's rank
    public String getRank() {
        return rank;
    }

    // Mutator for the card's rank
    public void setRank(String rank) {
        this.rank = rank;
    }

    // Override toString() method to provide a string representation of the card
    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    // Method to place a bet
    public static void placeBet(int amount) {
        if (amount > playerBalance) {
            throw new IllegalArgumentException("Insufficient funds for this bet.");
        }
        currentBet = amount;
        playerBalance -= amount;
        System.out.println("Bet placed: $" + amount);
    }

    // Method to settle the bet after the game
    public static void settleBet(boolean win, boolean tie) {
        if (tie) {
            // Return the bet amount if it's a tie
            playerBalance += currentBet;
            System.out.println("Bet amount returned. Balance is now: $" + playerBalance);
         }else if (win) {
            playerBalance += currentBet * 2; // Win: double the bet
            System.out.println("Bet settled. Balance is now: $" + playerBalance);
        } else {
            System.out.println("You lost. Balance is now: $" + playerBalance);
        }
        currentBet = 0; // Reset the bet amount after settling
    }

    // Method to check the player's balance
    public static int getPlayerBalance() {
        return playerBalance;
    }

    // Method to display current bet
    public static int getCurrentBet() {
        return currentBet;
    }
}