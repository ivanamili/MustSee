package mosis.ivana.mustsee;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;

import mosis.ivana.mustsee.DataModel.Category;

public class AddPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner= findViewById(R.id.newPlaceCategorySpinner);
        spinner.setAdapter(new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, Category.values()));
    }

    @Override
    public void onClick(View v) {

    }
}
