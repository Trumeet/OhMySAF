package moe.yuuta.ohmysaf;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

public class OhMySAF {
    @Nullable
    public static SafFile ohMyTree(@NonNull Context context, @NonNull Uri uri) {
        return ohMyDocument(DocumentFile.fromTreeUri(context, uri));
    }

    @Nullable
    public static SafFile ohMyFile(@NonNull Context context, @NonNull Uri uri) {
        return ohMyDocument(DocumentFile.fromSingleUri(context, uri));
    }

    @Nullable
    public static SafFile ohMyDocument(@Nullable DocumentFile file) {
        if (file == null) return null;
        return new SafFile(file);
    }
}
