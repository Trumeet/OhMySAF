package moe.yuuta.ohmysaf;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

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

    /**
     * Auto detect whatever the given uri is a tree uri or a file uri.
     */
    public static SafFile ohMyUri(@NonNull Context context, @NonNull Uri uri) {
        if (isTreeUri(uri)) {
            return ohMyTree(context, uri);
        } else {
            return ohMyFile(context, uri);
        }
    }

    public static boolean isTreeUri(@NonNull Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;
        try {
            DocumentsContract.getTreeDocumentId(uri);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
