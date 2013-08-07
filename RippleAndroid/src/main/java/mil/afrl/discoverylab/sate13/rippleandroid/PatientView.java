package mil.afrl.discoverylab.sate13.rippleandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * This class draws the patient view inside of the horizontal scrolling bar.
 * Created by harmonbc on 6/19/13.
 */
public class PatientView extends View {

    //Border for each grid square
    private static final int BORDER = 5;

    // Reference to patient
    private Patient mPatient;
    // Reference to context
    private Context mContext;
    // Colors
    private Paint mPaintBG, mPaintText, mPaintStatus;
    private static final Paint mPaintRed = new Paint();
    private static final Paint mPaintYellow = new Paint();
    private static final Paint mPaintGreen = new Paint();
    private static final Paint mBMPaint = new Paint(Paint.DITHER_FLAG);
    // Id
    private int mRowOrder;
    private Bitmap mBitmap = null;

    private enum DataFields {
        RESP_PM, PULSE_OX, BEATS_PM, TEMPERATURE
    }

    public PatientView(Context context, Patient patient, int i) {
        super(context);
        mContext = context;
        mPatient = patient;
        mRowOrder = i;
        init();
    }

    public int getPid() {
        return this.mPatient.getPid();
    }


    private void init() {
        // Setup paints
        mPaintBG = new Paint();
        mPaintText = new Paint();
        mPaintStatus = new Paint();

        mPaintText.setStyle(Paint.Style.STROKE);
        mPaintBG.setStyle(Paint.Style.FILL);
        mPaintStatus.setStyle(Paint.Style.FILL);

        mPaintRed.setStyle(Paint.Style.FILL);
        mPaintYellow.setStyle(Paint.Style.FILL);
        mPaintGreen.setStyle(Paint.Style.FILL);

        mPaintYellow.setStrokeWidth(3);
        mPaintRed.setStrokeWidth(3);
        mPaintGreen.setStrokeWidth(3);

        mPaintStatus.setColor(mPatient.getColor());

        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(20);
        mPaintText.setFakeBoldText(true);

        mPaintYellow.setTextSize(20);
        mPaintYellow.setFakeBoldText(true);

        mPaintBG.setColor(Color.BLACK);


        mPaintRed.setColor(Color.RED);
        mPaintYellow.setColor(Color.YELLOW);
        mPaintGreen.setColor(Color.GREEN);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d(Common.LOG_TAG, "Patient view draw");
        // get view height and width
        int vWidth = getWidth();
        int vHeight = getHeight();
        // get size for value rectangles
        int statHeight = (vHeight - 10) / 3;
        int statWidth = (int) (vWidth * .6);


        RectF rect = new RectF();
        // Draw border color
        rect.set(0, 0, vWidth - 2, vHeight);
        canvas.drawRoundRect(rect, 10, 10, mPaintStatus);

        // Draw inner background
        rect.set(BORDER, BORDER - 1, vWidth - (BORDER * 2), vHeight - (BORDER * 2) + 1);
        canvas.drawRoundRect(rect, 10, 10, mPaintBG);

        // Draw rectangle for temperature, formally respiration value
        rect.set(BORDER * 2, BORDER * 2, statWidth, (statHeight) - BORDER);
        //canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.RESP_PM));
        canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.TEMPERATURE));

        // Draw rectangle for pulse value
        rect.set(BORDER * 2, statHeight + BORDER * 2, statWidth, statHeight * 2 - BORDER);
        canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.BEATS_PM));

        // Draw rectangle for blood oxygen value
        rect.set(BORDER * 2, statHeight * 2 + BORDER * 2, statWidth, statHeight * 3 - BORDER);
        canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.PULSE_OX));

        //canvas.drawRect(statWidth, BORDER,vWidth-BORDER, vHeight-(BORDER*2), mPaintBG);

        // Draw temperature (formally respiration), pulse, and blood oxygen
        //canvas.drawText(mPatient.getRpm() + "", statWidth / 2, BORDER * 2 + statHeight / 2, mPaintText);
        canvas.drawText(mPatient.getTemperature() + "", statWidth / 2, BORDER * 2 + statHeight / 2, mPaintText);
        canvas.drawText(mPatient.getBpm() + "", statWidth / 2, BORDER * 2 + statHeight + statHeight / 2, mPaintText);
        canvas.drawText(mPatient.getO2() + "", statWidth / 2, BORDER * 2 + (statHeight * 2) + statHeight / 2, mPaintText);
        // Draw id
        if (this.mBitmap == null) {
            canvas.drawText(mRowOrder + "", statWidth + ((vWidth - statWidth - (BORDER * 2)) / 2), vHeight / 2, mPaintYellow);
        } else {
            canvas.drawBitmap(mBitmap, null,
                    //new Rect(vHeight, statWidth, statWidth + ((vWidth - statWidth - (BORDER * 2))), 0),
                    new Rect(vHeight - BORDER, statWidth - BORDER, vWidth - BORDER, BORDER),
                    mBMPaint);
        }
    }

    private Paint getColor(DataFields type) {
        Paint paint = null;

        switch (type) {
            case PULSE_OX:
                paint = getPulseOxPaint();
                break;
            case BEATS_PM:
                paint = getBeatsPMPaint();
                break;
            case RESP_PM:
                paint = getRespPMPaint();
                break;
            case TEMPERATURE:
                paint = getTemperaturePaint();
                break;
        }

        return paint;
    }

    private Paint getRespPMPaint() {
        int val = mPatient.getRpm();
        if (val < 26 && val > 11) {
            return mPaintGreen;
        } else if (val < 30 && val > 9) {
            return mPaintYellow;
        } else {
            return mPaintRed;
        }
    }

    private Paint getTemperaturePaint() {
        int val = mPatient.getTemperature();
        if (val <= 99 && val >= 97) {
            return mPaintGreen;
        } else if (val < 101 && val > 90) {
            return mPaintYellow;
        } else {
            return mPaintRed;
        }
    }

    private Paint getBeatsPMPaint() {
        int val = mPatient.getBpm();
        if (val < 120 && val > 60) {
            return mPaintGreen;
        } else if (val < 150 && val > 40) {
            return mPaintYellow;
        } else {
            return mPaintRed;
        }
    }

    private Paint getPulseOxPaint() {
        int val = mPatient.getO2();
        if (val > 92) {
            return mPaintGreen;
        }
        if (val > 88) {
            return mPaintYellow;
        } else {
            return mPaintRed;
        }
    }

    public void setmBitmap(Bitmap mBitmap) {
        if(this.mBitmap != null){
            this.mBitmap.recycle();
        }
        this.mBitmap = mBitmap;
    }
}
