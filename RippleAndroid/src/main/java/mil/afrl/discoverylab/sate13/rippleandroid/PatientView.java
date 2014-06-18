package mil.afrl.discoverylab.sate13.rippleandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * This class draws the patient view inside of the horizontal scrolling bar.
 * Created by harmonbc on 6/19/13.
 */
public class PatientView extends RelativeLayout {

    // View params in dp
    private static final int VIEW_PADDING = 10;
    private static final int VIEW_HEIGHT = 99;
    private static final int VIEW_WIDTH = 110;

    // Reference to patient
    private Patient mPatient;
    // Reference to context
    private Context mContext;
    // Colors
    private int colorRed;
    private int colorYellow;
    private int colorGreen;

    // Id
    private Bitmap mBitmap = null;
    // text views
    private TextView temperatureText;
    private TextView heartRateText;
    private TextView bloodOxText;
    private TextView idText;

    private enum DataFields {
        RESP_PM, BLOOD_OX, BEATS_PM, TEMPERATURE
    }

    public PatientView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public PatientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public PatientView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public int getPid() {
        return this.mPatient.getPid();
    }

    public String getPatientSrc() {
        return this.mPatient.getSrc();
    }

    public void setPatient(Patient patient) {
        mPatient = patient;
        String id = getPatientSrc();
        if (id != null && id.length() >= 4) {
            this.idText.setText(id.substring(id.length() - 4));
        }
        this.updateViewFields();
    }

    private void init() {
        // get colors from resources
        this.colorRed = getResources().getColor(R.color.red);
        this.colorYellow = getResources().getColor(R.color.yellow);
        this.colorGreen = getResources().getColor(R.color.win8_green);

        View v = inflate(this.mContext, R.layout.patient_view, this);

        //this.patientViewLayout = v.findViewById(R.id.patient_view_layout);
        this.temperatureText = (TextView) v.findViewById(R.id.patient_view_temperature);
        this.heartRateText = (TextView) v.findViewById(R.id.patient_view_heart_rate);
        this.bloodOxText = (TextView) v.findViewById(R.id.patient_view_sp02);
        this.idText = (TextView) v.findViewById(R.id.patient_view_id);
        this.setBackgroundResource(R.drawable.patient_border);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // set padding so patient vitals do not cover border
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VIEW_PADDING, metrics);
        this.setPadding(padding, padding, padding, padding);

        // set width and height of layout
        int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VIEW_HEIGHT, metrics);
        this.setMinimumHeight(minHeight);

        int minWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VIEW_WIDTH, metrics);
        this.setMinimumWidth(minWidth);

        // set margin on right to add a little separation between patient views
        int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
        // need layout params of parent view type
        TableRow.LayoutParams params = (TableRow.LayoutParams) this.getLayoutParams();
        if (params == null) {
            params = new TableRow.LayoutParams(minWidth, minHeight);
        }
        params.setMargins(0, 0, marginRight, 0);
        this.setLayoutParams(params);
    }

    public void updateViewFields() {
        // update temperature field
        int temp = this.mPatient.getTemperature();
        String tempString = "T: " + temp;
        if (temp > 999) {
            // too high
            tempString = "T: ---";
        }
        this.temperatureText.setText(tempString);
        this.temperatureText.setBackgroundColor(this.getColor(DataFields.TEMPERATURE));

        // update heart rate field
        int heartRate = this.mPatient.getBpm();
        String hrString = "HR: " + heartRate;
        if (heartRate >= 250) {
            // no reading
            hrString = "HR: ---";
        }
        this.heartRateText.setText(hrString);
        this.heartRateText.setBackgroundColor(this.getColor(DataFields.BEATS_PM));

        // update blood ox field
        int bloodOx = this.mPatient.getO2();
        String bloodOxString = "02: " + bloodOx;
        if (bloodOx >= 125) {
            // no reading
            bloodOxString = "O2: ---";
        }
        this.bloodOxText.setText(bloodOxString);
        this.bloodOxText.setBackgroundColor(this.getColor(DataFields.BLOOD_OX));

        // update patient color
        GradientDrawable bgDrawable = (GradientDrawable) this.getBackground();
        if (bgDrawable != null) {
            bgDrawable.setStroke(5, mPatient.getColor());
        }

        // check if patient is selected
        if(mPatient.isSelected()){
            this.idText.setTextColor(this.colorYellow);
            this.idText.setTypeface(this.idText.getTypeface(), Typeface.BOLD);
        } else {
            this.idText.setTextColor(getResources().getColor(R.color.white));
            this.idText.setTypeface(null, Typeface.NORMAL);
        }
    }


    private int getColor(DataFields type) {
        int paint = Color.WHITE;

        switch (type) {
            case BLOOD_OX:
                paint = getBloodOxBGColor();
                break;
            case BEATS_PM:
                paint = getBeatsPMBGColor();
                break;
            case RESP_PM:
                paint = getRespPMBGColor();
                break;
            case TEMPERATURE:
                paint = getTemperatureBGColor();
                break;
        }

        return paint;
    }

    private int getRespPMBGColor() {
        int val = mPatient.getRpm();
        if (val < 26 && val > 11) {
            return this.colorGreen;
        } else if (val < 30 && val > 9) {
            return this.colorYellow;
        } else {
            return this.colorRed;
        }
    }

    private int getTemperatureBGColor() {
        int val = mPatient.getTemperature();
        if (val <= 99 && val >= 97) {
            return this.colorGreen;
        } else if (val < 101 && val > 90) {
            return this.colorYellow;
        } else {
            return this.colorRed;
        }
    }

    private int getBeatsPMBGColor() {
        int val = mPatient.getBpm();
        if (val < 120 && val > 60) {
            return this.colorGreen;
        } else if (val < 150 && val > 40) {
            return this.colorYellow;
        } else {
            return this.colorRed;
        }
    }

    private int getBloodOxBGColor() {
        int val = mPatient.getO2();
        if (val > 92 && val <= 100) {
            return this.colorGreen;
        }
        if (val > 88 && val <= 100) {
            return this.colorYellow;
        } else {
            return this.colorRed;
        }
    }

    public void setmBitmap(Bitmap mBitmap) {
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
        }
        this.mBitmap = mBitmap;
    }
}
