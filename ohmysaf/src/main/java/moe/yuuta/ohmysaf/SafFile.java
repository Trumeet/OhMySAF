package moe.yuuta.ohmysaf;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.KITKAT;

public class SafFile extends File {
    private final DocumentFile mOrig;

    public SafFile(String pathname) {
        super(pathname);
        throw new UnsupportedOperationException("The Java constructor is not supported in SafFile");
    }

    public SafFile(String parent, String child) {
        super(parent, child);
        throw new UnsupportedOperationException("The Java constructor is not supported in SafFile");
    }

    public SafFile(File parent, String child) {
        super(parent, child);
        throw new UnsupportedOperationException("The Java constructor is not supported in SafFile");
    }

    public SafFile(URI uri) {
        super(uri);
        throw new UnsupportedOperationException("The Java constructor is not supported in SafFile");
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    SafFile(@NonNull DocumentFile orig) {
        super(orig.getUri().getPath());
        mOrig = orig;
    }

    @Override
    public String getName() {
        return mOrig.getName();
    }

    @Nullable
    @Override
    public String getParent() {
        final File parent = getParentFile();
        if (parent == null) return null;
        return parent.getAbsolutePath();
    }

    @Nullable
    @Override
    public File getParentFile() {
        if (mOrig.getParentFile() == null) return null;
        return new SafFile(mOrig.getParentFile());
    }

    @Override
    public String getPath() {
        return mOrig.getUri().getPath();
    }

    @Override
    public boolean isAbsolute() {
        return mOrig.getUri().isAbsolute();
    }

    @Override
    public String getAbsolutePath() {
        // TODO: Incorrect?
        return mOrig.getUri().getPath();
    }

    @Override
    public URI toURI() {
        return URI.create(mOrig.getUri().toString());
    }

    @Override
    public boolean canRead() {
        return mOrig.canRead();
    }

    @Override
    public boolean canWrite() {
        return mOrig.canWrite();
    }

    @Override
    public boolean exists() {
        return mOrig.exists();
    }

    @Override
    public boolean isDirectory() {
        return mOrig.isDirectory();
    }

    @Override
    public boolean isFile() {
        return mOrig.isFile();
    }

    @Override
    public boolean isHidden() {
        // TODO
        return getName().startsWith(".");
    }

    @Override
    public long lastModified() {
        return mOrig.lastModified();
    }

    @Override
    public long length() {
        return mOrig.length();
    }

    @Override
    public boolean createNewFile() throws IOException {
        // This should never be called.
        return true;
    }

    @Override
    public boolean delete() {
        return mOrig.delete();
    }

    @Override
    public String[] list() {
        return list(null);
    }

    @Override
    public String[] list(FilenameFilter filter) {
        File[] files = listFiles();
        List<String> names = new ArrayList<>(files.length);
        for (File file : files) {
            if (filter != null && !filter.accept(file.getParentFile(), file.getName())) continue;
            names.add(file.getName());
        }
        return names.toArray(new String[]{});
    }

    @Override
    public File[] listFiles() {
        return listFiles((FilenameFilter) null);
    }

    @Override
    public File[] listFiles(@Nullable FilenameFilter filter) {
        DocumentFile[] origFiles = mOrig.listFiles();
        if (origFiles.length <= 0) return new File[0];
        List<File> files = new ArrayList<>(origFiles.length);
        for (DocumentFile file : origFiles) {
            final DocumentFile parent = file.getParentFile();
            if (filter != null && !filter.accept(parent == null ? null : new SafFile(parent),
                    file.getName())) {
                continue;
            }
            files.add(new SafFile(file));
        }
        return files.toArray(new File[]{});
    }

    @Override
    public File[] listFiles(FileFilter filter) {
        DocumentFile[] origFiles = mOrig.listFiles();
        if (origFiles.length <= 0) return new File[0];
        List<File> files = new ArrayList<>(origFiles.length);
        for (DocumentFile file : origFiles) {
            final File f = new SafFile(file);
            if (filter != null && !filter.accept(f)) {
                continue;
            }
            files.add(f);
        }
        return files.toArray(new File[]{});
    }

    @Override
    public boolean mkdir() {
        // This should never be called because the file will only be gotten if this direction is exists.
        return true;
    }

    @Override
    public boolean mkdirs() {
        // This should never be called because the file will only be gotten if this direction is exists.
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return mOrig.getUri().toString();
    }

    @Override
    public Path toPath() {
        return super.toPath();
    }

    @Override
    public boolean renameTo(File dest) {
        return mOrig.renameTo(dest.getName());
    }

    @Nullable
    public SafFile createChildFile(@NonNull String mimeType, @NonNull String displayName) {
        final DocumentFile file = mOrig.createFile(mimeType, displayName);
        if (file == null) return null;
        return new SafFile(file);
    }

    @Nullable
    public SafFile createChildDirectory(@NonNull String displayName) {
        final DocumentFile file = mOrig.createDirectory(displayName);
        if (file == null) return null;
        return new SafFile(file);
    }

    @Nullable
    public String getType() {
        return mOrig.getType();
    }

    @Nullable
    public SafFile findFile(@NonNull String displayName) {
        final DocumentFile file = mOrig.findFile(displayName);
        if (file == null) return null;
        return new SafFile(file);
    }

    @Override
    public File getAbsoluteFile() {
        return super.getAbsoluteFile();
    }

    @Override
    public boolean setLastModified(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setReadOnly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setWritable(boolean writable, boolean ownerOnly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setWritable(boolean writable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setReadable(boolean readable, boolean ownerOnly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setReadable(boolean readable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setExecutable(boolean executable) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    public Uri getAndroidUri() {
        return mOrig.getUri();
    }

    @Override
    public boolean canExecute() {
        // TODO
        return false;
    }

    @Override
    public String getCanonicalPath() throws IOException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public File getCanonicalFile() throws IOException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public long getUsableSpace() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteOnExit() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTotalSpace() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public long getFreeSpace() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public URL toURL() throws MalformedURLException {
        return new URL(mOrig.getUri().toString());
    }

    @Nullable
    public ParcelFileDescriptor openFileDescriptor(@NonNull Context context, @NonNull String mode) throws FileNotFoundException {
        return context.getContentResolver().openFileDescriptor(getAndroidUri(), mode);
    }

    @Nullable
    public OutputStream openOutputStream(@NonNull Context context, @NonNull String mode) throws FileNotFoundException {
        return context.getContentResolver().openOutputStream(getAndroidUri(), mode);
    }

    @Nullable
    public InputStream openInputStream(@NonNull Context context) throws FileNotFoundException {
        return context.getContentResolver().openInputStream(getAndroidUri());
    }

    @RequiresApi(KITKAT)
    public void takePersistableUriPermission(@NonNull Context context, int modeFlags) {
        context.getContentResolver().takePersistableUriPermission(getAndroidUri(), modeFlags);
    }
}
