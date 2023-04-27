package ubi.com.sensorsdataacquisition.activities;

import android.annotation.SuppressLint;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import ubi.com.sensorsdataacquisition.R;
import ubi.com.sensorsdataacquisition.adapters.SensorsAdapter;
import ubi.com.sensorsdataacquisition.utils.SensorUtils;
import ubi.com.sensorsdataacquisition.utils.Variables;

public class PreferencesActivity extends AppCompatActivity {

    @BindView(R.id.time_to_start_preferences_et)
    TextInputEditText time_to_start_preferences;

    @BindView(R.id.recyclerview_sensors)
    RecyclerView recyclerViewSensors;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setTitle(R.string.edit_preferences);
        ButterKnife.bind(this);

        time_to_start_preferences.setText(Integer.toString((Variables.TIME_BEFORE / 1000)));

        time_to_start_preferences.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nao faz nada
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(""))
                    Variables.TIME_BEFORE = Integer.parseInt(s.toString()) * 1000;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nao faz nada
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewSensors.setLayoutManager(mLayoutManager);
        recyclerViewSensors.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dItem = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dItem.setDrawable(getResources().getDrawable(R.drawable.divider));
        recyclerViewSensors.addItemDecoration(dItem);
        SensorsAdapter sensorsAdapter = new SensorsAdapter(this, SensorUtils.getAvailableSensors(this));
        recyclerViewSensors.setAdapter(sensorsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SensorsAdapter sensorsAdapter = new SensorsAdapter(this, SensorUtils.getAvailableSensors(this));
        recyclerViewSensors.setAdapter(sensorsAdapter);
    }
}
