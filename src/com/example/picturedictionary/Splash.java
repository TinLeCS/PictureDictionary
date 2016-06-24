package com.example.picturedictionary;

import android.content.Intent;
import android.os.Bundle;
 
public class Splash extends AbstractActivity {
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.splash);
      Thread splashThread = new Thread() {
         @Override
         public void run() {
            try {
               int waited = 0;
               while (waited < 3000) {
                  sleep(100);
                  waited += 100;
               }
            } catch (InterruptedException e) {
               // do nothing
            } finally {
               finish();
               Intent intent = new Intent();
               intent.setClassName("com.example.picturedictionary", "com.example.picturedictionary.HomeActivity");
               startActivity(intent);
            }
         }
      };
      splashThread.start();
   }
}
