package com.example.willowevents;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.net.Uri;

public class FirebaseMediaRepository implements MediaRepository {

    private final StorageReference postersRoot;

    public FirebaseMediaRepository(FirebaseStorage storage) {

        this.postersRoot = storage.getReference().child("event_posters");
    }

    @Override
    public void uploadPoster(Uri uri, String eventId, StringCallback cb) {
        if (uri == null) {
            cb.onError(new IllegalArgumentException("Poster uri is null"));
            return;
        }

        StorageReference posterRef = postersRoot.child(eventId + ".jpg");

        posterRef.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return posterRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    String url = downloadUri.toString();
                    cb.onSuccess(url);
                })
                .addOnFailureListener(cb::onError);
    }
}
