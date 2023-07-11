package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int counter = 0;
        int min = 0;// We can set this to maximum higest value of integers. Interger.MAX_VALUE:
        int max = 0;// We can set this one to lowest int value of integers. Interger.MIN_VALUE:
        boolean flag = true;

        while (true){

             System.out.println("Please enter #"+(counter+1));
             boolean isNextInt = scanner.hasNextInt();

             if (isNextInt){
                 int number = scanner.nextInt();
                 if (flag){
                     flag = false;
                     min = number;
                     max = number;
                 }
                 counter ++;
                 if (number>max){
                     max =number;
                 }
                 if (number<min){
                     min = number;
                 }
             }
             else {
                 System.out.println("Minimum nuber is " + min);
                 System.out.println("Minimum nuber is " + max);
                 break;
             }
             scanner.nextLine();
         }
         scanner.close();
    }
}
