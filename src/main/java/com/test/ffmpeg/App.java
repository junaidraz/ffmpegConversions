package com.test.ffmpeg;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void showpercent() throws InterruptedException {
        int progress = 10;
        for(progress = 10; progress<=100; progress+=10) {
            System.out.print("\rPercent = " + (progress) + "%");
            Thread.sleep(1000);
        }
    }
    public static void main( String[] args ) throws InterruptedException {
        showpercent();
    }
}
