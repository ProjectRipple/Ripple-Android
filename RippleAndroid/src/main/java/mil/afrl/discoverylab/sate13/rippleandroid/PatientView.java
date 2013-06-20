package mil.afrl.discoverylab.sate13.rippleandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * Created by harmonbc on 6/19/13.
 */
public class PatientView extends View {
    private Patient mPatient;
    private Context mContext;
    private Paint mPaintBG, mPaintText;
    private int mRowOrder, tx, bx, ty, by;

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

        mPaintText.setStyle(Paint.Style.FILL);
        mPaintBG.setStyle(Paint.Style.FILL);

        mPaintText.setColor(Color.YELLOW);
        mPaintBG.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int vWidth = getWidth()-10;
        int vHeight = getHeight()-10;
        canvas.drawRect(0,0,vWidth,vHeight,mPaintBG);
        canvas.drawText(mRowOrder+"", 10, 10, mPaintText);
    }
}
