package temp;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

  /**
   * Calculate the sum of all prime numbers up to a given integer 'n'.
   */
  public static long sumOfPrimes(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("Input must be a non-negative integer.");
    }

    if (n < 2) {
      return 0; // No primes less than 2
    }

    // Use Sieve of Eratosthenes to calculate prime numbers up to n
    boolean[] isPrime = new boolean[n + 1];
    Arrays.fill(isPrime, true);
    isPrime[0] = isPrime[1] = false; // 0 and 1 are not primes

    for (int i = 2; i * i <= n; i++) {
      if (isPrime[i]) {
        for (int j = i * i; j <= n; j += i) {
          isPrime[j] = false;
        }
      }
    }

    // Calculate the sum of primes
    long sum = 0;
    for (int i = 2; i <= n; i++) {
      if (isPrime[i]) {
        sum += i;
      }
    }
    return sum;
  }
  

  /**
   * Converts a string to Title Case (i.e., capitalize the first letter of each word).
   *
   * @param input A string.
   * @return The input string converted to title case.
   * @see <a href="https://en.wikipedia.org/wiki/Title_case">Title_case</a>
   */

  public static String convertToTitleCase(String input) {
    if (input == null || input.isEmpty()) {
      // Handle null or empty input by returning an empty string
      return "";
    }

    // 1. Normalize whitespace by trimming and replacing multiple spaces with a single space
    return Arrays.stream(input.trim().split("\\s+"))
        .map(String::toLowerCase) // Convert words to lowercase (just in case)
        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1)) // Convert to title case
        .collect(Collectors.joining(" ")); // Join words with a single space
  }

  /**
   * Groups an array of strings into anagrams.
   *
   * @param arr Array of strings.
   * @return A list of grouped anagrams.
   */
  public static Set<Set<String>> groupAnagrams(String[] arr) {
    if (arr == null || arr.length == 0) return new HashSet<>();

    Map<String, Set<String>> anagramMap = new HashMap<>();

    for (String word : arr) {
      // Sort the characters in the word to create a key
      char[] chars = word.toCharArray();
      Arrays.sort(chars);
      String sortedKey = new String(chars);

      // Group words with the same sortedKey together
      anagramMap.computeIfAbsent(sortedKey, k -> new HashSet<>()).add(word);
    }








    return new HashSet<>(anagramMap.values());
  }


}