package com.veviosys.vdigit.configuration;

public class CustomThread extends Thread {
    public void run() {
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (i<10) {
             
            try {
                //Wait for one sec so it doesn't print too fast
                
                // Thread²
                this.sleep(2000);
                // Thread.sleep(3000);
               

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
}
