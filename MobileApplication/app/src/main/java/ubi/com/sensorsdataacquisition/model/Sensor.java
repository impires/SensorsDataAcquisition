package ubi.com.sensorsdataacquisition.model;

import java.io.Serializable;

public class Sensor implements Serializable {
    private String name;
    private String subtitle;
    private boolean checked;

    public Sensor() {
    }

    public Sensor(String name, String subtitle, boolean checked) {
        this.name = name;
        this.subtitle = subtitle;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
