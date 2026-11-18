import org.apache.commons.codec.digest.MurmurHash3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HashApp {

    private static final Map<Integer, Integer> lastIndex = new HashMap<>();
    private static final Map<Integer, Integer> lastSize = new HashMap<>();

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: java HashApp <number-of-hash-buckets> <bucket-size>");
            System.exit(1);
        }

        int n = Integer.parseInt(args[0]);
        int s = Integer.parseInt(args[1]);
        if(n <= 0 || s <= 0) {
            System.err.println("Both <number-of-hash-buckets> and <bucket-size> must be positive integers");
            System.exit(1);
        }
        try(Scanner sc = new Scanner(System.in)){
            while(true){
                System.out.print("Please enter the string: ");
                if(!sc.hasNextLine()){
                    break;
                }
                String str = sc.nextLine();
                if(str.isBlank()){
                    continue;
                }

                int bucket = findBucketNumber(str, n);
                try {
                    String files = addToBucket(bucket, str, s);
                    System.out.println(str + " added to " + files);
                } catch (IOException e) {
                    System.err.println("Could not write to bucket " + bucket + ": " + e.getMessage());
                }
            }
        }

    }

    private static int findBucketNumber(String text, int divisor) {
        int hash = MurmurHash3.hash32x86(text.getBytes(StandardCharsets.UTF_8));

        int remainder = Integer.remainderUnsigned(hash, divisor);
        if(remainder == 0) remainder = divisor;
        return remainder;
    }

    private static void appendToFile(String fileName, String text) throws IOException {
        Files.writeString(Path.of(fileName), text,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static String addToBucket(int bucket, String text, int s) throws IOException {
        if(!lastIndex.containsKey(bucket)) {
            int index = 0;
            while(new File(bucket + "_" + (index + 1) + ".txt").exists()) index++;
            String last = index == 0 ?
                    bucket + ".txt" :
                    bucket + "_" + index + ".txt";

            lastIndex.put(bucket, index);
            lastSize.put(bucket, new File(last).exists() ? Files.readString(Path.of(last)).length() : 0);
        }

        String written = "";
        while(!text.isEmpty()) {
            if(lastSize.get(bucket) >= s) {
                lastIndex.put(bucket, lastIndex.get(bucket) + 1);
                lastSize.put(bucket, 0);
            }
            int index = lastIndex.get(bucket);
            String fileName = index == 0 ? bucket + ".txt" : bucket + "_" + index + ".txt";

            int fittingSize = Math.min(s - lastSize.get(bucket), text.length());
            String fittingPart = text.substring(0, fittingSize);
            appendToFile(fileName, fittingPart);
            lastSize.put(bucket, lastSize.get(bucket) + fittingSize);
            text = text.substring(fittingSize);

            written += written.isEmpty() ? fileName : ", " + fileName;
        }
        return written;
    }
}