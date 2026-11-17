import org.apache.commons.codec.digest.MurmurHash3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class HashApp {

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: java HashApp <number-of-hash-buckets> <bucket-size>");
            System.exit(1);
        }

        int n = Integer.parseInt(args[0]);
        int s = Integer.parseInt(args[1]);
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
                String fileName = bucket + ".txt";
                try {
                    appendToFile(fileName, str);
                    System.out.println(str + " added to " + fileName);
                } catch (IOException e) {
                    System.err.println("Could not write to " + fileName + ": " + e.getMessage());
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
}