package com.example.sneha.mortgagecalculator;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView seekBarTextView;
    private EditText amountBorrowedEditText;
    private RadioGroup loanTermRadioGroup;
    private CheckBox taxInsuranceCheckBox;
    private Button calculateButton;
    private RadioButton selectedRadioButton;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //update the interest rate based on the seekbar value
        updateSeekBar();

        //adding listener to the Calculate button
        addListenerToCalculateBtn();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void updateSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBarTextView = (TextView) findViewById((R.id.seekBarValue));
        seekBar.setProgress(50);

        //to set the value of the textView field to the selected seek bar value
        //textView.setText(seekBar.getProgress());

        //adding listener to seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double progressValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress/10.0;
                String progressValueString = String.valueOf(progressValue);
                seekBarTextView.setText(progressValueString + "%");
                //Toast.makeText()
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String progressValueString = String.valueOf(progressValue);
                seekBarTextView.setText(progressValueString + "%");
            }
        });
    }

    public void addListenerToCalculateBtn() {
        amountBorrowedEditText = (EditText) findViewById(R.id.amountEditText);
        loanTermRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        taxInsuranceCheckBox = (CheckBox) findViewById(R.id.checkBox);
        calculateButton = (Button) findViewById(R.id.button);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double monthlyPayment = 0;

                //principal
                double principal = 0;
                try {
                    principal = Double.parseDouble(amountBorrowedEditText.getText().toString());
                } catch (java.lang.NumberFormatException e) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.error_invalid_amount), Toast.LENGTH_LONG).show();
                    return;
                }

                //monthly interest
                double monthlyInterestRate = Double.parseDouble(seekBarTextView.getText().toString().replace('%',' ')) / 1200;

                //gets the checked radio button from the radio group
                int selectedRadioButtonId = loanTermRadioGroup.getCheckedRadioButtonId();

                //gets the selected radioButton from the Id
                selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonId);

                //gets the value of the radio button selected
                int loanTermInYears = Integer.parseInt(selectedRadioButton.getText().toString());
                int loanTermInMonths = loanTermInYears * 12;

                if (monthlyInterestRate != 0) {
                    if (taxInsuranceCheckBox.isChecked()) {
                        monthlyPayment = (principal * (monthlyInterestRate / (1 - Math.pow(1 + monthlyInterestRate, -loanTermInMonths)))) + ((0.1 / 100) * principal);
                    } else {
                        monthlyPayment = (principal * (monthlyInterestRate / (1 - Math.pow(1 + monthlyInterestRate, -loanTermInMonths))));
                    }
                } else if (monthlyInterestRate == 0) {
                    if (taxInsuranceCheckBox.isChecked()) {
                        monthlyPayment = (principal / loanTermInMonths) + ((0.1 / 100) * principal);
                    } else {
                        monthlyPayment = (principal / loanTermInMonths);
                    }
                }
                //Toast.makeText(MainActivity.this, String.valueOf(monthlyPayment), Toast.LENGTH_SHORT).show();

                TextView mortgageTextView = (TextView)findViewById(R.id.mortgageDisplay);
                mortgageTextView.setText( String.format("%s %1.2f",
                                            getResources().getText(R.string.mortgage_payment),
                                            monthlyPayment));


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sneha.mortgagecalculator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sneha.mortgagecalculator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
