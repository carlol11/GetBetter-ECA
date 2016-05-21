package com.geebeelicious.geebeelicious.models.vaccination;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by mgmalana on 21/05/2016.
 */
public class VaccinationHelper {
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}
