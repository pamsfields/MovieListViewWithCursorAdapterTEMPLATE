package com.clara.movielistviewwithcursoradapter;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;

public class MovieActivity extends AppCompatActivity implements MovieCursorAdapter.RatingChangedListener {

	private static final String TAG = "MOVIE ACTIVITY";
	DatabaseManager dbManager;
	MovieCursorAdapter cursorListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);

		//Create DatabaseManager object
		dbManager = new DatabaseManager(this);

		Button addNew = (Button) findViewById(R.id.add_movie_button);
		final EditText newMovieNameET = (EditText) findViewById(R.id.add_movie_name);
		final RatingBar newMovieRB = (RatingBar) findViewById(R.id.add_movie_rating_bar);

		final ListView movieList = (ListView) findViewById(R.id.movie_list_view);
		// create cursor
		Cursor cursor = dbManager.getAllMovies();
		//create CursorAdapter
		cursorListAdapter = new MovieCursorAdapter(this, cursor, true);
		//TODO Configure ListView to use this adapter
		movieList.setAdapter(cursorListAdapter);

		addNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = newMovieNameET.getText().toString();
				float rating = newMovieRB.getRating();

				//Add this movie to the database
				dbManager.addMovie(name, rating); //TODO detect errors and handle appropriately
				//Update list
				cursorListAdapter.changeCursor(dbManager.getAllMovies());
			}
		});

	}

	public void notifyRatingChanged(int movieID, float rating) {

		//Update DB, and then update the cursor for the ListView if necessary.
		dbManager.updateRating(movieID, rating);

		//Just to make sure the list and DB are in sync.
		//This program works fine without its call, but only because
		//the changes the user makes to the list are the same as the
		//list should show.
		//
		//If your program changes data in the database and the list
		//does need to update, like in the product DB app, then you'll
		//definitely need to recreate the cursor for the last adapter.
		cursorListAdapter.changeCursor(dbManager.getAllMovies());
	}


	//Don't forget these! Close and re-open DB as Activity pauses/resumes.

	@Override
	protected void onPause(){
		super.onPause();
		dbManager.close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		dbManager = new DatabaseManager(this);
	}
}
