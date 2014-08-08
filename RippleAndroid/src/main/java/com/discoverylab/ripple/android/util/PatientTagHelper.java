package com.discoverylab.ripple.android.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Helper class to determine what body part the user selected.
 *
 * Created by james on 7/16/14.
 */
public class PatientTagHelper {

    // Tolerance of color match since get pixel may not return 100% exact color on every device
    private static final int COLOR_TOLERANCE = 25;
    // Colors corresponding to each body part
    private static final int COLOR_FRONT_HEAD = Color.argb(255, 255, 255, 0);
    private static final int COLOR_FRONT_TORSO = Color.argb(255, 0, 255, 0);
    private static final int COLOR_FRONT_RIGHT_ARM = Color.argb(255, 255, 0, 0);
    private static final int COLOR_FRONT_LEFT_ARM = Color.argb(255, 0, 0, 255);
    private static final int COLOR_FRONT_RIGHT_LEG = Color.argb(255, 255, 0, 255);
    private static final int COLOR_FRONT_LEFT_LEG = Color.argb(255, 0, 255, 255);

    public enum BODY_PARTS {
        NONE("None"), FRONT_HEAD("Head - Front"), FRONT_TORSO("Torso - Front"),
        FRONT_RIGHT_ARM("Right Arm - Front"), FRONT_LEFT_ARM("Left Arm - Front"),
        FRONT_RIGHT_LEG("Right Leg - Front"), FRONT_LEFT_LEG("Left Leg - Front");

        private final String printableString;

        private BODY_PARTS(String printableString){
            this.printableString = printableString;
        }

        public String getPrintableString(){
            return this.printableString;
        }

    }

    /**
     * Get the body part selected based on the image and touch coordinates.
     *
     * @param image Image to pick color from
     * @param x X coordinate of touch point on image
     * @param y Y coordinate of touch point on image
     * @return Body part selected based on color at (x,y) or BODY_PARTS.NONE if no part was selected.
     */
    public static BODY_PARTS getBodyPartSelected(Bitmap image, int x, int y) {
        BODY_PARTS rtnValue = BODY_PARTS.NONE;
        // get color of point user touched
        int touchColor = getTouchColor(image, x, y);


        // Check with closeMatch as display colors may not be exactly those specified due to scaling

        if (touchColor == -1) {
            // invalid color
            rtnValue = BODY_PARTS.NONE;
        } else if (closeMatch(COLOR_FRONT_HEAD, touchColor, COLOR_TOLERANCE)) {
            rtnValue = BODY_PARTS.FRONT_HEAD;
        } else if (closeMatch(COLOR_FRONT_TORSO, touchColor, COLOR_TOLERANCE)) {
            rtnValue = BODY_PARTS.FRONT_TORSO;
        } else if (closeMatch(COLOR_FRONT_RIGHT_ARM, touchColor, COLOR_TOLERANCE)) {
            rtnValue = BODY_PARTS.FRONT_RIGHT_ARM;
        } else if (closeMatch(COLOR_FRONT_LEFT_ARM, touchColor, COLOR_TOLERANCE)) {
            rtnValue = BODY_PARTS.FRONT_LEFT_ARM;
        } else if (closeMatch(COLOR_FRONT_RIGHT_LEG, touchColor, COLOR_TOLERANCE)) {
            rtnValue = BODY_PARTS.FRONT_RIGHT_LEG;
        } else if (closeMatch(COLOR_FRONT_LEFT_LEG, touchColor, COLOR_TOLERANCE)) {
            rtnValue = BODY_PARTS.FRONT_LEFT_LEG;
        }

        return rtnValue;
    }

    /**
     * Return the color of the pixel at coordinates (x,y) of the specified ImageView
     *
     * @param image Bitmap to pull color from
     * @param x     X coordinate of the pixel to retrieve
     * @param y     Y coordinate of the pixel to retrieve
     * @return Color of the pixel at (x,y) of the Imageview or -1 if point is outside the image area
     */
    private static int getTouchColor(Bitmap image, int x, int y) {
        int rtnValue = -1;
        if (x > 0 && y > 0) {
            if (x < image.getWidth() && y < image.getHeight()) {
                rtnValue = image.getPixel(x, y);
            }

        }
        return rtnValue;
    }

    /**
     * Check if color1 and color2 are a close match (difference within the tolerance per channel)
     *
     * @param color1    An ARGB color value
     * @param color2    ARGB color to compare with the first
     * @param tolerance Allowable difference between the colors for each color channel
     * @return true if colors are within the tolerance of each other, false otherwise
     */
    private static boolean closeMatch(int color1, int color2, int tolerance) {
        boolean rtnValue = true;
        if (Math.abs(Color.alpha(color1) - Color.alpha(color2)) > tolerance) {
            rtnValue = false;
        } else if (Math.abs(Color.red(color1) - Color.red(color2)) > tolerance) {
            rtnValue = false;
        } else if (Math.abs(Color.green(color1) - Color.green(color2)) > tolerance) {
            rtnValue = false;
        } else if (Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance) {
            rtnValue = false;
        }
        return rtnValue;
    }
}
