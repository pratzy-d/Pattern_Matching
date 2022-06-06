package project_bmh;

import java.util.HashMap;
import java.util.Map;

public class BMH {
  public int match(String T, String P) {
    int n = T.length(); // Length of Text T
    int m = P.length(); // Length of Pattern P

    // Minimum Length Error Check
    if(m == 0 || n == 0 || n < m) return -1;

    //////////////////////////////////////////////////////////////////////
    // Initialization, create Last Occurence Hash Table (as per class notes)
    //////////////////////////////////////////////////////////////////////
    Map<Character, Integer> lastOccurenceTable = new HashMap<>();

    // NOTE: I am checking the char in the Last Occurence HashTable,
    // if not found, I take the value as -1 in while loop below

    //For each character in the Pattern P, calculate maxShift and update the hash table
    for (int index = 0; index < m; index++)
    {
      // Store the highest index of the character
      lastOccurenceTable.put(P.charAt(index), index);
    }

    //////////////////////////////////////////////////////////////////////
    // Start the Comparison
    //////////////////////////////////////////////////////////////////////

    //Start with the end of the pattern
    int i = m - 1;
    int j = m - 1;

    while (i < n) {
      // Character matched.
      // Return i if it's a complete match; otherwise, keep checking
      if (T.charAt(i) == P.charAt(j)) {
        if (j == 0) { // Full Match
          return i;
        }
        else {
          i--;
          j--;
        }
      }
      else {
        // Shift i by the following
        // Length pf pattern - minimum of j or one more than Last Occurence index of that character

        int lastOccurence = -1;
        if (lastOccurenceTable.get(T.charAt(i)) != null) // If present in the hash table
          lastOccurence = lastOccurenceTable.get(T.charAt(i));

        i = i + (m - Math.min(j, 1 + lastOccurence));
        //Reset to the end of the pattern again
        j = m - 1;
      }
    }

    return -1;
  }
}
