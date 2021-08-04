package me.edgrrrr.de;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.print("You aren't supposed to click me D:\nYou're supposed to put me with the other plugins :D\nClosing in ");
            for (int i = 5; i > -0; i--) {
                System.out.printf("%s...", i);
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.print("Bye bye :(");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
