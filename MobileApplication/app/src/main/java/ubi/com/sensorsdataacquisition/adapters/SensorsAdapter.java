package ubi.com.sensorsdataacquisition.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ubi.com.sensorsdataacquisition.R;
import ubi.com.sensorsdataacquisition.model.Sensor;
import ubi.com.sensorsdataacquisition.utils.Variables;

public class SensorsAdapter extends RecyclerView.Adapter<SensorsAdapter.MyViewHolder> {
    private List<Sensor> sensors;
    private Context ctx;

    public SensorsAdapter(Context ctx, List<Sensor> sensors) {
        this.sensors = sensors;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Sensor sensor = sensors.get(position);
        if (sensor != null) {
            holder.nameSensor.setText(sensor.getName());
            holder.subtitleSensor.setText(sensor.getName());
            holder.checkBox.setVisibility((sensor.isChecked()) ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    @Override
    public long getItemId(int position) {
        return sensors.get(position).getName().hashCode();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title_sensor)
        TextView nameSensor;

        @BindView(R.id.subtitle_sensor)
        TextView subtitleSensor;

        @BindView(R.id.checked_sensor)
        ImageView checkBox;

        MyViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, itemView);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (sensors.get(getAdapterPosition()).getName()) {
                case "Accelerometer":
                    Variables.accEnabled = !Variables.accEnabled;
                    sensors.get(getAdapterPosition()).setChecked(!sensors.get(getAdapterPosition()).isChecked());
                    notifyDataSetChanged();
                    break;
                case "Magnetometer":
                    Variables.magEnabled = !Variables.magEnabled;
                    sensors.get(getAdapterPosition()).setChecked(!sensors.get(getAdapterPosition()).isChecked());
                    notifyDataSetChanged();
                    break;
                case "Gyroscope":
                    Variables.gyroEnabled = !Variables.gyroEnabled;
                    sensors.get(getAdapterPosition()).setChecked(!sensors.get(getAdapterPosition()).isChecked());
                    notifyDataSetChanged();
                    break;
                case "GPS":
                    Variables.gpsEnabled = !Variables.gpsEnabled;
                    sensors.get(getAdapterPosition()).setChecked(!sensors.get(getAdapterPosition()).isChecked());
                    notifyDataSetChanged();
                    break;
            }

        }
    }

}