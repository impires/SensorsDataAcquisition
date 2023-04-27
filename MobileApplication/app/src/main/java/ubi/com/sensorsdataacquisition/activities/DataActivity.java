package ubi.com.sensorsdataacquisition.activities;

import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.satsuware.usefulviews.LabelledSpinner;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ubi.com.sensorsdataacquisition.R;
import ubi.com.sensorsdataacquisition.utils.Variables;

public class DataActivity extends AppCompatActivity {

    @BindView(R.id.id_change_data_et)
    TextInputEditText id_change_data;

    @BindView(R.id.idade_change_data_et)
    TextInputEditText age_change_data;

    @BindView(R.id.altura_change_data_et)
    TextInputEditText height_change_data;

    @BindView(R.id.peso_change_data_et)
    TextInputEditText peso_change_data;

    @BindView(R.id.test_change_data_et)
    TextInputEditText test_change_data;

    @BindView(R.id.gender_spinner)
    LabelledSpinner gender_change_data;

    private String gender_selected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setTitle(R.string.edit_data);
        ButterKnife.bind(this);

        if(!Variables.id_study.equals("")) {
            id_change_data.setText(Variables.id_study);
        }

        if(!Variables.age_person.equals("")) {
            age_change_data.setText(Variables.age_person);
        }

        CharSequence[] genders = new CharSequence[2];
        genders[0] = getString(R.string.male);
        genders[1] = getString(R.string.female);

        gender_change_data.setItemsArray(genders);
        gender_change_data.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                gender_selected = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        if(!Variables.height_person.equals("")) {
            height_change_data.setText(Variables.height_person);
        }

        if(!Variables.weight_person.equals("")) {
            this.peso_change_data.setText(Variables.weight_person);
        }

        if(!Variables.test.equals("")) {
            this.test_change_data.setText(Variables.test);
        }
        
        if(!Variables.gender_person.equals("Male")) {
            this.gender_change_data.setSelection(0);
        } else {
            this.gender_change_data.setSelection(1);
        }
    }

    public void onClickSaveData(View view) {
        Variables.id_study = Objects.requireNonNull(id_change_data.getText()).toString();
        Variables.weight_person = Objects.requireNonNull(peso_change_data.getText()).toString();
        Variables.height_person = Objects.requireNonNull(height_change_data.getText()).toString();
        Variables.age_person = Objects.requireNonNull(age_change_data.getText()).toString();
        Variables.test = Objects.requireNonNull(test_change_data.getText()).toString();
        Variables.gender_person = gender_selected;
        finish();
    }
}
