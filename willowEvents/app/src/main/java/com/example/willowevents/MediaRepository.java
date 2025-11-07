package com.example.willowevents;

import android.net.Uri;


public interface MediaRepository {
    interface StringCallback { void onSuccess(String value); void onError(Throwable t); }
    void uploadPoster(Uri localUri, String eventId, StringCallback cb);
}
