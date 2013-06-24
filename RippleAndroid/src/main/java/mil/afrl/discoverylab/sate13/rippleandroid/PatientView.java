package mil.afrl.discoverylab.sate13.rippleandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * This class draws the patient view inside of the horizontal scrolling bar.
 * Created by harmonbc on 6/19/13.
 */
public class PatientView extends View {

    private static final int BORDER = 5; //Border for each grid square
    private static int statHeight, statWidth;

    private static final Paint mPaintRed = new Paint();
    private static final Paint mPaintYellow = new Paint();
    private static final Paint mPaintGreen = new Paint();

    private Patient mPatient;
    private Context mContext;
    private Paint mPaintBG, mPaintText,mPaintStatus;
    private int mRowOrder, tx, bx, ty, by;

    private enum DataFields{
        RESP_PM, PULSE_OX, BEATS_PM
    }
    public PatientView(Context context, Patient patient, int i) {
        super(context);
        mContext = context;
        mPatient = patient;
        mRowOrder = i;
        init();
    }

    private void init(){
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
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        statHeight = (getHeight()-10)/3;
        statWidth = (int) (getWidth()*.6);

        int vWidth = getWidth();
        int vHeight = getHeight();

        RectF rect = new RectF();
        rect.set(0,  0,  vWidth-2, vHeight);
        canvas.drawRoundRect(rect, 10, 10, mPaintStatus);
        rect.set(BORDER,BORDER-1,vWidth-(BORDER*2),vHeight-(BORDER*2)+1);
        canvas.drawRoundRect(rect, 10, 10, mPaintBG);
        rect.set(BORDER*2, BORDER*2, statWidth, (statHeight) - BORDER);
        canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.RESP_PM));
        rect.set(BORDER * 2, statHeight + BORDER * 2, statWidth, statHeight * 2 - BORDER);
        canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.BEATS_PM));
        rect.set(BORDER*2,  statHeight*2+BORDER*2,   statWidth,  statHeight*3-BORDER);
        canvas.drawRoundRect(rect, 10, 10, getColor(DataFields.PULSE_OX));

        //canvas.drawRect(statWidth, BORDER,vWidth-BORDER, vHeight-(BORDER*2), mPaintBG);

        canvas.drawText(mPatient.getRpm()+"", statWidth/2, BORDER*2+statHeight/2, mPaintText);
        canvas.drawText(mPatient.getBpm()+"",statWidth/2, BORDER*2+statHeight+statHeight/2, mPaintText);
        canvas.drawText(mPatient.getO2()+"",statWidth/2, BORDER*2+(statHeight*2)+statHeight/2, mPaintText);

        canvas.drawText(mRowOrder+"", statWidth+((vWidth-statWidth-(BORDER*2))/2), vHeight/2, mPaintYellow);
    }

    private Paint getColor(DataFields type){
        Paint paint = null;

        switch(type){
            case PULSE_OX: paint = getPulseOxPaint(); break;
            case BEATS_PM: paint = getBeatsPMPaint(); break;
            case RESP_PM: paint = getRespPMPaint(); break;
        }

        return paint;
    }

    private Paint getRespPMPaint() {
        int val = mPatient.getRpm();
        if(val < 26 && val > 11) return mPaintGreen;
        else if(val < 30 && val > 9) return mPaintYellow;
        else return mPaintRed;
    }

    private Paint getBeatsPMPaint() {
        int val = mPatient.getBpm();
        if(val<120 && val>60) return mPaintGreen;
        else if(val<150 && val >40) return mPaintYellow;
        else return mPaintRed;
    }

    private Paint getPulseOxPaint() {
        int val = mPatient.getO2();
        if(val > 92 ) return mPaintGreen;
        if(val > 88 ) return mPaintYellow;
        else return mPaintRed;
    }
}
