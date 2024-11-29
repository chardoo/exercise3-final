/**
 * Briefly explained, under each method
 * - 3.1 a, b, c, d, e
 */
package exercise3.impl;

import java.util.ArrayList;
import java.util.List;

public class Encoding {
    /**
     * Compresses the passed values using Differential Encoding.
     */
    public static int[] encodeDiff(int[] numbers) {


        // -Checking if the input array is null or empty
        if (numbers == null || numbers.length == 0) {

            // returning an empty array if so.
            return new int[0];
        }

        // -Creating a new array encoded of the same length as the input.
        int[] encoded = new int[numbers.length];

        // -Storing the first number as-is.
        encoded[0] = numbers[0];

        // -For each subsequent number,

        for (int i = 1; i < numbers.length; i++) {
            //  calculating the difference from its predecessor
            //    and storing it in "encoded['array index']".
            encoded[i] = numbers[i] - numbers[i - 1];
        }

        // -Finally, returning the encoded array
        return encoded;
    }

    /**
     * Decompresses values previously compressed via Differential Encoding.
     */
    public static int[] decodeDiff(int[] numbers) {

        // -Checking if the input array is null or empty
        if (numbers == null || numbers.length == 0) {
            // returning an empty array if so.
            return new int[0];
        }

        // -Creating a new array decoded of the same length as the input.
        int[] decoded = new int[numbers.length];

        // -Storing the first number as-is, because in differential encoding,
        //    the first number is stored without modification.
        decoded[0] = numbers[0];

        // -For each subsequent number (starting from index 1):
        for (int i = 1; i < numbers.length; i++) {

            // -adding the current encoded value (which is a difference)
            //      to the previous decoded value.
            decoded[i] = decoded[i - 1] + numbers[i];
        }

        // -Finally, returning the decoded array
        return decoded;
    }

    /**
     * Compresses the passed values using Variable Byte Encoding.
     */
    public static byte[] encodeVB(int[] numbers) {

        // -Created a List of type "byte" to store the encoded bytes
        List<Byte> ourbytestream = new ArrayList<>();

        // -To iterate over each number in the input array "numbers"
        for (int n : numbers) {

            // -Now encoding each number using Variable Byte Encoding
            //     and add to the "ourbytestream"
            ourbytestream.addAll(vbEncodeNumber(n));
        }

        // -Convert the list of bytes to a byte array
        byte[] result = new byte[ourbytestream.size()];
        for (int i = 0; i < ourbytestream.size(); i++) {
            result[i] = ourbytestream.get(i);
        }

        // -Returning the encoded byte array
        return result;
    }

    private static List<Byte> vbEncodeNumber(int n) {

        // -Created a List to store bytes for a single number
        List<Byte> bytes = new ArrayList<>();

        // -a while loop to encode the number into a series of bytes
        while (true) {

            // -now, adding the least significant 7 bits of n as a byte
            bytes.add(0, (byte) (n % 128));

            // -If "n" is less than 128, break the loop as encoding is complete
            if (n < 128) break;

            // -Otherwise, we continue encoding by dividing "n" by 128
            n = n / 128;
        }

        // -Marked the last byte by setting its high-order bit to 1
        bytes.set(bytes.size() - 1, (byte) (bytes.get(bytes.size() - 1) + 128));

        // -returning the list of encoded bytes,
        //   for the invoked number
        return bytes;
    }

    /**
     * Decompresses values previously compressed via Variable Byte Encoding.
     */
    public static int[] decodeVB(byte[] vbs) {
        // -Created a List to store decoded numbers
        List<Integer> numbers = new ArrayList<>();

        // -Declaring and initializing a variable "n"
        //    to accumulate the current number being decoded
        int n = 0;

        // -To iterate over each byte in the input array "vbs"
        for (byte b : vbs) {

            // -Checking if the high-order bit of b is not set (b < 128)
            if ((b & 0xFF) < 128) {
                // -Accumulate value: shift left by 7 bits and add current byte value
                n = 128 * n + (b & 0xFF);
            } else {
                // - when high-order bit is set,
                n = 128 * n + ((b & 0xFF) - 128);

                //   finalizing current number
                numbers.add(n);

                // and reset n
                n = 0;
            }
        }

        // -Converting the list of integers to an int array and return it
        return numbers.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 3.1 e)
     * Main method to demonstrate encoding and decoding.
     *
     * Expected Output:-
     *
     * Original sequence:
     * 1 7 56 134 256 268 384 472 512 648
     * Its size: 40 bytes
     *
     * i) Differential Encoding:
     * 1 6 49 78 122 12 116 88 40 136
     * Its size: 40 bytes
     *
     * ii) VB compression:
     * 129 135 184 1 134 2 128 2 140 3 128 3 216 4 128 5 136
     * Its size: 17 bytes
     *
     * iii) Differential Encoding + VB compression:
     * 129 134 177 206 250 140 244 216 168 1 136
     * its size: 11 bytes
     *
     * Results Overview for the implemented methods as follows :-
     *
     * i) Differential Encoding helps reduce the range of values but does not compress data size.
     * ii) VB Compression reduces data size by using fewer bytes for smaller numbers.
     * iii) Combined Encoding leverages both techniques to achieve maximum compression by :
     * - first reducing value magnitudes
     * - then applying efficient byte-level compression
     */
    public static void main(String[] args) {

        int[] seq = {1, 7, 56, 134, 256, 268, 384, 472, 512, 648};

        System.out.println("\nOriginal sequence:");
        for (int num : seq) {
            System.out.print(num + " ");
        }

        //(10 integers * 4 bytes each)
        System.out.println("\nOriginal size: " + (seq.length * 4) + " bytes");


        // 3.1 e) i. Differential Encoding
        int[] diffEncoded = encodeDiff(seq);
        System.out.println("\ni) Differential Encoding:");
        for (int num : diffEncoded) {
            System.out.print(num + " ");
        }
        System.out.println("\nIts size: " + (diffEncoded.length * 4) + " bytes");


        // 3.1 e) ii. VB compression
        byte[] vbEncoded = encodeVB(seq);
        System.out.println("\nii) VB compression:");
        for (byte b : vbEncoded) {
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println("\nIts size: " + vbEncoded.length + " bytes");


        // 3.1 e) iii. Differential Encoding and subsequent VB compression
        byte[] diffVbEncoded = encodeVB(diffEncoded);
        System.out.println("\niii) Differential & subsequent VB compression:");
        for (byte b : diffVbEncoded) {
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println("\nIts size: " + diffVbEncoded.length + " bytes");
    }
}