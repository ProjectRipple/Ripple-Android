package com.discoverylab.ripple.android.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Note Item for an image note.
 * <p/>
 * Created by james on 7/16/14.
 */
public class NoteItemImage implements NoteItem {

    // System path to image
    private final String imageName;

    public NoteItemImage(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Get the full image path to the image file.
     *
     * @return Full image path to the file.
     */
    public String getImagePath() {
        // Make sure this matches the save path of images in CameraFragment
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Common.PHOTO_DIR).getPath()
                + File.separator + imageName;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.IMAGE;
    }

    /**
     *
     * @return JsonObject for this note item without image as base64.
     */
    public JsonObject getJsonObjectNoImage(){
        JsonObject object = new JsonObject();

        object.addProperty(JSONTag.NOTE_ITEM_TYPE, this.getNoteType().toString());

        object.addProperty(JSONTag.NOTE_ITEM_FILE, this.imageName);

        return object;

    }

    @Override
    public JsonObject getJsonObject() {

        JsonObject object = getJsonObjectNoImage();

        if (Common.SEND_IMAGE_BASE64) {
            // Grab image
            Bitmap bm = BitmapFactory.decodeFile(getImagePath());
            // check that image was decoded
            if (bm != null) {

                int orgHeight = bm.getHeight();
                int orgWidth = bm.getWidth();

                double aspectRatio = (double) orgWidth / orgHeight;

                int targetWidth = 640;
                int destHeight = (int) (targetWidth / aspectRatio);

                // create byte array of image as jpeg
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap scaledBm = null;

                if (orgWidth > targetWidth) {
                    // reduce image further
                    scaledBm = Bitmap.createScaledBitmap(bm, targetWidth, destHeight, true);
                    scaledBm.compress(Bitmap.CompressFormat.JPEG, 98, baos);
                } else {
                    // use original
                    bm.compress(Bitmap.CompressFormat.JPEG, 98, baos);
                }


                byte[] b = baos.toByteArray();
                // encode image
                String encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
                // add image to json object
                object.addProperty(JSONTag.NOTE_ITEM_IMG, encodedImage);

                // recycle bitmaps now that we are done
                bm.recycle();
                if (scaledBm != null) {
                    scaledBm.recycle();
                }
            }
        }

        return object;
    }
}
