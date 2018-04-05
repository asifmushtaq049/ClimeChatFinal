package com.climesoft.climechat.common;

/**
 * Created by Asif on 6/12/2017.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

/**
 * Created by Asif Mushtaq on 2/25/2017.
 */

public class ContactUtil {

    public static String getContactNameByPhone(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = "Unknown";
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }
        if(!cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
}