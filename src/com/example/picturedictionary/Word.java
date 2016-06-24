package com.example.picturedictionary;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Word extends AbstractActivity {
	private ImageButton play;
	private Resources res;
	private AssetManager assetManager;
	private InputStream stream;
	private TextView definition;
	private ImageView imageView;
	private String imageList[];
    private Field pronunciationList[] = R.raw.class.getFields();
    private int index = 0;
    private boolean alreadyPlayed = false;
    private final String DEFAULT_CATEGORY = "Foods";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word);
		final String category;
		//Set default category for testing
		if (getIntent().getExtras() == null)
			category = DEFAULT_CATEGORY;
		else
			category = getIntent().getExtras().getString("category");
		
        res = getResources();
        assetManager = res.getAssets();
        try {
			imageList = assetManager.list(category);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		definition = (TextView) findViewById(R.id.definition);
		imageView = (ImageView) findViewById(R.id.imageView);
		final ImageButton next = (ImageButton) findViewById(R.id.next);
		ImageButton previous = (ImageButton) findViewById(R.id.previous);
		
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (index == imageList.length - 1)
					index = -1;
				String word = getDefinition(imageList[++index]);
				loadImage(imageList[index], category);
				definition.setText(word);
				try {
					playPronunciation(word.toLowerCase());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (index == 0)
					index = imageList.length;
				index = index - 2;
				next.performClick();
			}
		});
		
		String firstWord = getDefinition(imageList[index]);				 
		
		loadImage(imageList[index], category);
		definition.setText(firstWord);		
		try {
			playPronunciation(firstWord.toLowerCase());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void loadImage(String fileName, String category){
		try {
			stream = assetManager.open(category + "/" + fileName);
			Drawable image = Drawable.createFromStream(stream, fileName.replace(".jpg", ""));
			imageView.setImageDrawable(image);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void playPronunciation(String word) throws IllegalArgumentException, IllegalAccessException {
		int id = 0;
		for (Field p : pronunciationList)
			if (p.getName().equals(word)){
				id = p.getInt(p);
				break;
			}
		final int soundID = id;		
	
		play = (ImageButton) findViewById(R.id.play);
		
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				MediaPlayer mp = MediaPlayer.create(Word.this, soundID);  
				mp.start();
				alreadyPlayed = true;
			}

		});

	}

    public String getDefinition(String s){
        return s.substring(1 + s.indexOf('-')).replace('_', ' ').replace(".jpg", "");
    }
    
    // get index for testing previous and next button
    public int getIndex(){
    	return index;
    }
    
    // set index for testing previous and next button	
    public void setIndex(int index){
    	this.index = index;
    }
    
    // method for determining whether pronunciation was already played or not
    public boolean isPlayed(){
    	return alreadyPlayed;
    }
    
    public ImageView getImageView(){
    	return imageView;
    }

}
