package Utils;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputHelper {
    private final Scanner input=new Scanner(System.in);

    public int readInt(String msg){
        while(true){
            try{
                System.out.print(msg);
                return Integer.parseInt(input.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Please enter a valid number.");
            }
        }
    }
    public double readDouble(String msg){
        while(true){
            try{
                System.out.print(msg);
                double value = Double.parseDouble(input.nextLine());
                if(!Double.isFinite(value)){
                    System.out.println("Please enter a valid finite number.");
                    continue;
                }
                return value;
            }catch (NumberFormatException e){
                System.out.println("Please enter a valid number.");
            }
        }
    }
    public String readString(String msg){
        System.out.print(msg);
        return input.nextLine();
    }
    public BigDecimal readBigDecimal(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                BigDecimal value = new BigDecimal(input.nextLine());

//                if (value.compareTo(BigDecimal.ZERO) <= 0) {
//                    System.out.println("Please enter a positive number.");
//                    continue;
//                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    public Double readOptionalDouble(String msg) {
        while (true) {
            String input = readString(msg);

            if (input.isBlank()) {
                return null;
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public BigDecimal readOptionalBigDecimal(String msg) {
        while (true) {
            String input = readString(msg);

            if (input.isBlank()) {
                return null;
            }

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid price.");
            }
        }
    }
}