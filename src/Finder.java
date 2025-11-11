import java.io.BufferedReader;
import java.io.IOException;
/**
 * Finder
 * A puzzle written by Zach Blick
 * for Adventures in Algorithms
 * At Menlo School in Atherton, CA
 *
 * Completed by: Vikram Saluja
 **/

public class Finder {

    private static final String INVALID = "INVALID KEY";
    private static final double LIMIT = 0.5;
    // Default table size
    private static final int defulatTableSize = 1000003; // prime near 1 million

    private int tableSize;
    private int numRecords;
    // Parallel arrays to store keys and their corresponding values
    private String[] keys;
    private String[] values;


    // Constructor initializes the hash table with the default size and zero records
    public Finder() {
        // Start with default size
        tableSize = defulatTableSize;
        // Create array to hold keys
        keys = new String[tableSize];
        // Start with an empty table
        values = new String[tableSize];
        numRecords = 0;
    }

    // Hash function using Horner's method
    public int hash(String key) {
        // Set base radix
        int radix = 31;
        // running hash value
        long h = 0;
        for (int i = 0; i < key.length(); i++) {
            // multiply previous hash by radix and add next character
            h = (h * radix + key.charAt(i)) % tableSize;
        }
        return (int) h;
    }

    public void add(String key, String value) {
        // if the table is at least half full, then resize it to double its size
        if ((double) numRecords / tableSize >= LIMIT) {
            resize();
        }

        // Complete the initial index using the hash function
        int index = hash(key);
        // If this slot already contains the same key, update its value
        while (keys[index] != null) {
            // If this slot already contains the same key, update its value
            if (keys[index].equals(key)) {
                // Done updating
                values[index] = value;

                return;
            }
            // Otherwise, move to the next slot
            index = (index + 1) % tableSize;
        }

        // Found an empty slot --> insert new key and values
        keys[index] = key;
        values[index] = value;
        // Increment number of stored records
        numRecords++;
    }

    // Looks up the value associated with a given key
    public String get(String key) {
        // Start at the hashed index
        int index = hash(key);
        // Remember starting position to catch full loops
        int start = index;

        while (keys[index] != null) {
            // If this slot's key matches the query key, return its value
            if (keys[index].equals(key)) {
                return values[index];
            }

            // Otherwise, continue probing to next slot
            index = (index + 1) % tableSize;

            // If we loop around the entire table, stop
            if (index == start) {
                // Prevent infinite loops when table is full
                break;
            }
        }
        // If not found return invalid key
        return INVALID;
    }

    public void resize() {
        // Store old table size
        int oldSize = tableSize;
        // Double the table's capacity
        tableSize *= 2;

        // Store references to old keys and value arrays
        String[] oldKeys = keys;
        String[] oldValues = values;

        // Create new, larger empty arrays
        keys = new String[tableSize];
        values = new String[tableSize];
        // Reset record counter before reinserting
        numRecords = 0;

        // Rehash old keys into new table
        for (int i = 0; i < oldSize; i++) {
            // Only reinsert non null keys
            if (oldKeys[i] != null) {
                add(oldKeys[i], oldValues[i]);
            }
        }
    }

    // Read CSV and build the hash table
    public void buildTable(BufferedReader br, int keyCol, int valCol) throws IOException {
        // Hold each line read from file
        String line;
        // Read each line in the CSV file until EOF
        while ((line = br.readLine()) != null) {
            // Split the line by comas into an array of strings
            String[] parts = line.split(",");
            // Ensure that both the key and value columns exist
            if (parts.length > Math.max(keyCol, valCol)) {
                // Extract the key column
                String key = parts[keyCol];
                // Extract the value column
                String value = parts[valCol];
                // Insert the key value pair into the hash table
                add(key, value);
            }
        }
        // Close the reader after finishing
        br.close();
    }

    // Return the value for a query key
    public String query(String key) {
        // Return result of internal get() method
        return get(key);
    }
}