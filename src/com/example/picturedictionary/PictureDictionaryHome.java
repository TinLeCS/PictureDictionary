package com.example.picturedictionary;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class PictureDictionaryHome extends AbstractActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_dictionay_home);

		ImageButton action = (ImageButton) findViewById(R.id.action);
		ImageButton transport = (ImageButton) findViewById(R.id.transport);
		ImageButton food = (ImageButton) findViewById(R.id.food);
		ImageButton animal = (ImageButton) findViewById(R.id.animal);

		action.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						Word.class);
				nextScreen.putExtra("category", "Actions");
				startActivity(nextScreen);
			}
		});

		transport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						Word.class);
				nextScreen.putExtra("category", "Transports");
				startActivity(nextScreen);
			}
		});
		
		food.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						Word.class);
				nextScreen.putExtra("category", "Foods");
				startActivity(nextScreen);
			}
		});
		
		animal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent nextScreen = new Intent(getApplicationContext(),
						Word.class);
				nextScreen.putExtra("category", "Animals");
				startActivity(nextScreen);
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
