package com.example.renewable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class FillDataActivity extends AppCompatActivity {
    String answer1 = "0", answer2 = "0", answer3 = "0", answer4 = "0", answer5 = "0",
            answer6 = "", answer7 = "", answer8 = "", answer9 = "", answer10 = "", answer11 = "",
            answer12 = "", answer13 = "", answer14 = "", answer15 = "", answer16 = "",
            answer17 = "", answer18 = "", asnwer19 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);
    }

    public void onRadioButtonClicked1(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.simpleRadioButton:
                if (checked)
                    answer1 = "1";
                break;
            case R.id.simpleRadioButton1:
                if (checked)
                    answer1 = "0";
                break;
        }

    }

    public void onRadioButtonClicked2(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB21:
                if (checked)
                    answer2 = "1";
                break;
            case R.id.rB22:
                if (checked)
                    answer2 = "0";
                break;
        }
    }

    public void onRadioButtonClicked3(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB31:
                if (checked)
                    answer3 = "1";
                break;
            case R.id.rB32:
                if (checked)
                    answer3 = "0";
                break;
        }
    }

    public void onRadioButtonClicked4(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB41:
                if (checked)
                    answer4 = "1";
                break;
            case R.id.rB42:
                if (checked)
                    answer4 = "0";
                break;
        }
    }

    public void onRadioButtonClicked5(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB51:
                if (checked)
                    answer5 = "1";
                break;
            case R.id.rB52:
                if (checked)
                    answer5 = "0";
                break;
        }
    }

    public void onRadioButtonClicked6(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB61:
                if (checked)
                    answer6 = "1";
                break;
            case R.id.rB62:
                if (checked)
                    answer6 = "0";
                break;
        }
    }

    public void onRadioButtonClicked7(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB71:
                if (checked)
                    answer7 = "1";
                break;
            case R.id.rB72:
                if (checked)
                    answer7 = "0";
                break;
        }
    }

    public void onRadioButtonClicked8(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB81:
                if (checked)
                    answer8 = "1";
                break;
            case R.id.rB82:
                if (checked)
                    answer8 = "0";
                break;
        }
    }

    public void onRadioButtonClicked9(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB91:
                if (checked)
                    answer9 = "1";
                break;
            case R.id.rB92:
                if (checked)
                    answer9 = "0";
                break;
        }
    }

}