package com.scowluga.android.rcacc.sync;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by User on 12/30/2016.
 */

public interface RemoteDataStore {
    InputStream download() throws IOException;
    void upload(File feed) throws IOException;
}
