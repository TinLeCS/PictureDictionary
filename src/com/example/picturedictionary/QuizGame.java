package com.example.picturedictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QuizGame extends AbstractActivity {
	// String used when logging error messages
	private static final String TAG = "QuizGame Activity";

	private List<String> fileNameList; // Item file names
	private List<String> quizItemsList; // names of words in quiz
	private Map<String, Boolean> CategoriesMap; // which categories are enabled
	private String correctAnswer; // correct name for the current Item
	private int totalGuesses; // number of guesses made
	private int correctAnswers; // number of correct guesses
	private int guessRows; // number of rows displaying choices
	private Random random; // random number generator
	private Handler handler; // used to delay loading next Item
	private Animation shakeAnimation; // animation for incorrect guess

	private TextView answerTextView; // displays Correct! or Incorrect!
	private TextView questionNumberTextView; // shows current question #
	private ImageView itemImageView; // displays a Item
	private TableLayout buttonTableLayout; // table of answer Buttons

	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call the superclass's method
		setContentView(R.layout.activity_quiz_game); // inflate the GUI

		fileNameList = new ArrayList<String>(); // list of image file names
		quizItemsList = new ArrayList<String>(); // Items in this quiz
		CategoriesMap = new HashMap<String, Boolean>(); // HashMap of categories
		guessRows = 1; // default to one row of choices
		random = new Random(); // initialize the random number generator
		handler = new Handler(); // used to perform delayed operations

		// load the shake animation that's used for incorrect answers
		shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.incorrect_shake);
		shakeAnimation.setRepeatCount(3); // animation repeats 3 times

		// get array of categories from strings.xml
		String[] categoryNames = getResources().getStringArray(
				R.array.CategoriesList);

		// by default, words are chosen from all categories
		for (String category : categoryNames)
			CategoriesMap.put(category, true);

		// get references to GUI components
		questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
		itemImageView = (ImageView) findViewById(R.id.ItemImageView);
		buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
		answerTextView = (TextView) findViewById(R.id.answerTextView);

		// set questionNumberTextView's text
		questionNumberTextView.setText(getResources().getString(
				R.string.question)
				+ " 1 " + getResources().getString(R.string.of) + " 10");

		resetQuiz(); // start a new quiz
	} // end method onCreate

	// set up and start the next quiz
	private void resetQuiz() {
		// use the AssetManager to get the image Item
		// file names for only the enabled categories
		AssetManager assets = getAssets(); // get the app's AssetManager
		fileNameList.clear(); // empty the list

		try {
			Set<String> categories = CategoriesMap.keySet(); // get Set of
																// categories

			// loop through each category
			for (String category : categories) {
				if (CategoriesMap.get(category)) // if category is enabled
				{
					// get a list of all Item image files in this category
					String[] paths = assets.list(category);

					for (String path : paths)
						fileNameList.add(path.replace(".jpg", ""));
				} // end if
			} // end for
		} // end try
		catch (IOException e) {
			Log.e(TAG, "Error loading image file names", e);
		} // end catch

		correctAnswers = 0; // reset the number of correct answers made
		totalGuesses = 0; // reset the total number of guesses the user made
		quizItemsList.clear(); // clear prior list of quiz words

		// add 10 random file names to the quizItemsList
		int itemCounter = 1;
		int numberOfPictures = fileNameList.size(); // get number of pictures

		while (itemCounter <= 10) {
			int randomIndex = random.nextInt(numberOfPictures); // random index

			// get the random file name
			String fileName = fileNameList.get(randomIndex);

			// if the category is enabled and it hasn't already been chosen
			if (!quizItemsList.contains(fileName)) {
				quizItemsList.add(fileName); // add the file to the list
				++itemCounter;
			} // end if
		} // end while

		loadNextItem(); // start the quiz by loading the first item
	} // end method resetQuiz

	// after the user guesses a correct item, load the next item
	private void loadNextItem() {
		// get file name of the next item and remove it from the list
		String nextImageName = quizItemsList.remove(0);
		correctAnswer = nextImageName; // update the correct answer

		answerTextView.setText(""); // clear answerTextView

		// display the number of the current question in the quiz
		questionNumberTextView.setText(getResources().getString(
				R.string.question)
				+ " "
				+ (correctAnswers + 1)
				+ " "
				+ getResources().getString(R.string.of) + " 10");

		// extract the category from the next image's name
		String category = nextImageName
				.substring(0, nextImageName.indexOf('-'));

		// use AssetManager to load next image from assets folder
		AssetManager assets = getAssets(); // get app's AssetManager
		InputStream stream; // used to read in item images

		try {
			// get an InputStream to the asset representing the next item
			stream = assets.open(category + "/" + nextImageName + ".jpg");

			// load the asset as a Drawable and display on the itemImageView
			Drawable item = Drawable.createFromStream(stream, nextImageName);
			itemImageView.setImageDrawable(item);
		} // end try
		catch (IOException e) {
			Log.e(TAG, "Error loading " + nextImageName, e);
		} // end catch

		// clear prior answer Buttons from TableRows
		for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
			((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();

		Collections.shuffle(fileNameList); // shuffle file names

		// put the correct answer at the end of fileNameList
		int correct = fileNameList.indexOf(correctAnswer);
		fileNameList.add(fileNameList.remove(correct));

		// get a reference to the LayoutInflater service
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// add 3, 6, or 9 answer Buttons based on the value of guessRows
		for (int row = 0; row < guessRows; row++) {
			TableRow currentTableRow = getTableRow(row);

			// place Buttons in currentTableRow
			for (int column = 0; column < 3; column++) {
				// inflate guess_button.xml to create new Button
				Button newGuessButton = (Button) inflater.inflate(
						R.layout.guess_button, null);

				// get name name and set it as newGuessButton's text
				String fileName = fileNameList.get((row * 3) + column);
				newGuessButton.setText(getItemName(fileName));

				// register answerButtonListener to respond to button clicks
				newGuessButton.setOnClickListener(guessButtonListener);
				currentTableRow.addView(newGuessButton);
			} // end for
		} // end for

		// randomly replace one Button with the correct answer
		int row = random.nextInt(guessRows); // pick random row
		int column = random.nextInt(3); // pick random column
		TableRow randomTableRow = getTableRow(row); // get the TableRow
		String itemName = getItemName(correctAnswer);
		((Button) randomTableRow.getChildAt(column)).setText(itemName);
	} // end method loadNextitem

	// returns the specified TableRow
	private TableRow getTableRow(int row) {
		return (TableRow) buttonTableLayout.getChildAt(row);
	} // end method getTableRow

	// parses the name item file name and returns the name name
	private String getItemName(String name) {
		return name.substring(name.indexOf('-') + 1).replace('_', ' ');
	} // end method getItemName

	// called when the user selects an answer
	private void submitGuess(Button guessButton) {
		String guess = guessButton.getText().toString();
		String answer = getItemName(correctAnswer);
		++totalGuesses; // increment the number of guesses the user has made

		// if the guess is correct
		if (guess.equals(answer)) {
			++correctAnswers; // increment the number of correct answers

			// display "Correct!" in green text
			answerTextView.setText(answer + "!");
			answerTextView.setTextColor(getResources().getColor(
					R.color.correct_answer));

			disableButtons(); // disable all answer Buttons

			// if the user has correctly identified 10 items
			if (correctAnswers == 10) {
				// create a new AlertDialog Builder
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setTitle(R.string.reset_quiz); // title bar string

				// set the AlertDialog's message to display game results
				builder.setMessage(String.format("%d %s, %.02f%% %s",
						totalGuesses, getResources()
								.getString(R.string.guesses),
						(1000 / (double) totalGuesses), getResources()
								.getString(R.string.correct)));

				builder.setCancelable(false);

				// add "Reset Quiz" Button
				builder.setPositiveButton(R.string.reset_quiz,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								resetQuiz();
							} // end method onClick
						} // end anonymous inner class
				); // end call to setPositiveButton

				// create AlertDialog from the Builder
				AlertDialog resetDialog = builder.create();
				resetDialog.show(); // display the Dialog
			} // end if
			else // answer is correct but quiz is not over
			{
				// load the next item after a 1-second delay
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						loadNextItem();
					}
				}, 1000); // 1000 milliseconds for 1-second delay
			} // end else
		} // end if
		else // guess was incorrect
		{
			// play the animation
			itemImageView.startAnimation(shakeAnimation);

			// display "Incorrect!" in red
			answerTextView.setText(R.string.incorrect_answer);
			answerTextView.setTextColor(getResources().getColor(
					R.color.incorrect_answer));
			guessButton.setEnabled(false); // disable the incorrect answer
		} // end else
	} // end method submitGuess

	// utility method that disables all answer Buttons
	private void disableButtons() {
		for (int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
			TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
			for (int i = 0; i < tableRow.getChildCount(); ++i)
				tableRow.getChildAt(i).setEnabled(false);
		} // end outer for
	} // end method disableButtons

	// create constants for each menu id
	private final int CHOICES_MENU_ID = Menu.FIRST;
	private final int categories_MENU_ID = Menu.FIRST + 1;

	// called when the user accesses the options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.quiz_game, menu);
		super.onCreateOptionsMenu(menu);

		// add two options to the menu - "Choices" and "categories"
		menu.add(Menu.NONE, CHOICES_MENU_ID, Menu.NONE, R.string.choices);
		menu.add(Menu.NONE, categories_MENU_ID, Menu.NONE, R.string.categories);

		return true; // display the menu
	} // end method onCreateOptionsMenu

	// called when the user selects an option from the menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch the menu id of the user-selected option
		switch (item.getItemId()) {
		case CHOICES_MENU_ID:
			// create a list of the possible numbers of answer choices
			final String[] possibleChoices = getResources().getStringArray(
					R.array.guessesList);

			// create a new AlertDialog Builder and set its title
			AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(this);
			choicesBuilder.setTitle(R.string.choices);

			// add possibleChoices's items to the Dialog and set the
			// behavior when one of the items is clicked
			choicesBuilder.setItems(R.array.guessesList,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							// update guessRows to match the user's choice
							guessRows = Integer.parseInt(possibleChoices[item]
									.toString()) / 3;
							resetQuiz(); // reset the quiz
						} // end method onClick
					} // end anonymous inner class
					); // end call to setItems

			// create an AlertDialog from the Builder
			AlertDialog choicesDialog = choicesBuilder.create();
			choicesDialog.show(); // show the Dialog
			return true;

		case categories_MENU_ID:
			// get array of categories
			final String[] categoryNames = CategoriesMap.keySet().toArray(
					new String[CategoriesMap.size()]);

			// boolean array representing whether each category is enabled
			boolean[] categoriesEnabled = new boolean[CategoriesMap.size()];
			for (int i = 0; i < categoriesEnabled.length; ++i)
				categoriesEnabled[i] = CategoriesMap.get(categoryNames[i]);

			// create an AlertDialog Builder and set the dialog's title
			AlertDialog.Builder categoriesBuilder = new AlertDialog.Builder(
					this);
			categoriesBuilder.setTitle(R.string.categories);

			// replace _ with space in category names for display purposes
			String[] displayNames = new String[categoryNames.length];
			for (int i = 0; i < categoryNames.length; ++i)
				displayNames[i] = categoryNames[i].replace('_', ' ');

			// add displayNames to the Dialog and set the behavior
			// when one of the items is clicked
			categoriesBuilder.setMultiChoiceItems(displayNames,
					categoriesEnabled,
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							// include or exclude the clicked category
							// depending on whether or not it's checked
							CategoriesMap.put(categoryNames[which].toString(),
									isChecked);
						} // end method onClick
					} // end anonymous inner class
					); // end call to setMultiChoiceItems

			// resets quiz when user presses the "Reset Quiz" Button
			categoriesBuilder.setPositiveButton(R.string.reset_quiz,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int button) {
							resetQuiz(); // reset the quiz
						} // end method onClick
					} // end anonymous inner class
			); // end call to method setPositiveButton

			// create a dialog from the Builder
			AlertDialog categoriesDialog = categoriesBuilder.create();
			categoriesDialog.show(); // display the Dialog
			return true;
		} // end switch

		return super.onOptionsItemSelected(item);
	} // end method onOptionsItemSelected

	// called when a guess Button is touched
	private OnClickListener guessButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			submitGuess((Button) v); // pass selected Button to submitGuess
		} // end method onClick
	}; // end answerButtonListener
} // end itemQuizGame

