package com.example.picturedictionary;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		Button btnDict = (Button) findViewById(R.id.button1);
		Button btnQuiz = (Button) findViewById(R.id.button2);
		Button btnAb = (Button) findViewById(R.id.button3);
		Button btnEx = (Button) findViewById(R.id.button4);
		
		btnDict.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						PictureDictionaryHome.class);
				startActivity(nextScreen);
			}
		});

		btnAb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						About.class);
				startActivity(nextScreen);
			}
		});

		btnQuiz.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						QuizGame.class);
				startActivity(nextScreen);
			}
		});
		
		btnEx.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	            finish();
	            System.exit(0);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
