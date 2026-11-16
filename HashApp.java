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
                System.out.println(str);
            }
        }

    }
}