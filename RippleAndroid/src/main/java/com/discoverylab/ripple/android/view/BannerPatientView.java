package com.discoverylab.ripple.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.object.Patient;

/**
 * TODO: document your custom view class.
 */
public class BannerPatientView extends RelativeLayout {

    // Reference to patient
    private Patient mPatient;
    // Colors
    private int colorRed;
    private int colorYellow;
    private int colorGreen;
    // Text views
    private TextView patientIdText;
    private TextView patientStatusText;
    // View dimensions from resources
    private int viewWidthPixels;
    private int viewHeightPixels;
    private int viewPaddingPixels;
    private int viewMarginRightPixels;

    public BannerPatientView(Context context) {
        super(context);
        init(null, 0);
    }

    public BannerPatientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BannerPatientView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // get Resources object
        Resources resources = getResources();
        // get colors from resources
        this.colorRed = resources.getColor(R.color.red);
        this.colorYellow = resources.getColor(R.color.yellow);
        this.colorGreen = resources.getColor(R.color.win8_green);

        // get view dimensions
        this.viewWidthPixels = resources.getDimensionPixelSize(R.dimen.patient_banner_view_width);
        this.viewHeightPixels = resources.getDimensionPixelSize(R.dimen.patient_banner_view_height);
        this.viewPaddingPixels = resources.getDimensionPixelSize(R.dimen.patient_banner_view_padding);
        this.viewMarginRightPixels = resources.getDimensionPixelSize(R.dimen.patient_banner_view_margin_right);

        // inflate view and attach to this object
        View v = inflate(getContext(), R.layout.view_banner_patient, this);

        // get text views and set background
        this.patientIdText = (TextView) v.findViewById(R.id.patient_banner_view_id);
        this.patientStatusText = (TextView) v.findViewById(R.id.patient_banner_view_status);
        this.setBackgroundResource(R.drawable.patient_border);

        // set width, height, padding, and right margin
        this.setPadding(this.viewPaddingPixels, this.viewPaddingPixels, this.viewPaddingPixels, this.viewPaddingPixels);
        this.setMinimumHeight(this.viewHeightPixels);
        this.setMinimumWidth(this.viewWidthPixels);

        this.setGravity(Gravity.CENTER);

        // need layout params of parent view type
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(this.viewWidthPixels, this.viewHeightPixels);
        }

        params.setMargins(0, 0, this.viewMarginRightPixels, 0);
        this.setLayoutParams(params);

    }

    public void setPatient(Patient patient) {
        this.mPatient = patient;
        String id = this.mPatient.getPatientId();
        if (id != null) {
            if (id.length() > 6) {
                this.patientIdText.setText(id.substring(id.length() - 6));
            } else {
                this.patientIdText.setText(id);
            }
        }
        this.updateViewFields();
    }

    private void updateViewFields() {

        // set patient status
        this.patientStatusText.setText("Not Attended.");

        // set patient color
        GradientDrawable bgDrawable = (GradientDrawable) this.getBackground();
        if (bgDrawable != null) {
            bgDrawable.setStroke(5, mPatient.getTriageColor());
        }
    }
}
