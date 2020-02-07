package com.vfs.fingerprint.veridium.veridium;

import android.app.Application;
import android.content.Context;

import com.veridiumid.sdk.VeridiumSDK;
import com.veridiumid.sdk.activities.DefaultVeridiumSDKModelFactory;
import com.veridiumid.sdk.defaultdata.VeridiumSDKDataInitializer;
import com.veridiumid.sdk.fourfexport.VeridiumSDKFourExportFInitializer;
import com.veridiumid.sdk.model.exception.SDKInitializationException;

public class VeridiumDemoAppExport extends Application {

    private static final String LOG_TAG = VeridiumDemoAppExport.class.getName();

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        });

        Context appContext = getApplicationContext();

        try {
            // TODO Complete with your license key
            String fourfLicence = "hC5tXiqzRXf75NQXa868KV9gdl9rBuXUW5eI2IOrl4PZsfjulYfAkZG2nCqFBzl+Va3Ce5bvkl3qlNO6CLlxAHsiZGV2aWNlRmluZ2VycHJpbnQiOiJETklDSWpuNzh3WDZTaTdJdmdEOElsaWN4bzVsb0wrRCtibjhoZWc4TUFrPSIsImxpY2Vuc2UiOiJrMnkvWmp5MDRVbEx4dU51KzVFOUc1TGlQQjJDTVpBSFFDci90VnpoNVBoNGpGVkRqTWQrMW80TWFSdFIvZjhxK3ZMSUIwUlRlbzVaVzRBZ21IdkFBM3NpZEhsd1pTSTZJa0pKVDB4SlFsTWlMQ0p1WVcxbElqb2lORVlpTENKc1lYTjBUVzlrYVdacFpXUWlPakUxTnpnMk5UQTNNREl6T0RZc0ltTnZiWEJoYm5sT1lXMWxJam9pVmtaVElFZHNiMkpoYkNJc0ltTnZiblJoWTNSSmJtWnZJam9pSWl3aVkyOXVkR0ZqZEVWdFlXbHNJam9pYm1sMGFXNXphRUIyWm5ObmJHOWlZV3d1WTI5dElpd2ljM1ZpVEdsalpXNXphVzVuVUhWaWJHbGpTMlY1SWpvaU5tWnZlVWgwZW1GTVJEZEZlRFoyVjBJeFNrVnZjRlJNY0VFNFUzVndMMWc0T1ZaYU9VRnlVVmhPV1QwaUxDSnpkR0Z5ZEVSaGRHVWlPakUxTnpFNU5qRTJNREF3TURBc0ltVjRjR2x5WVhScGIyNUVZWFJsSWpveE5UZ3dOREk0T0RBd01EQXdMQ0puY21GalpVVnVaRVJoZEdVaU9qRTFPREV5T1RJNE1EQXdNREFzSW5WemFXNW5VMEZOVEZSdmEyVnVJanBtWVd4elpTd2lkWE5wYm1kR2NtVmxVa0ZFU1ZWVElqcG1ZV3h6WlN3aWRYTnBibWRCWTNScGRtVkVhWEpsWTNSdmNua2lPbVpoYkhObExDSmlhVzlzYVdKR1lXTmxSWGh3YjNKMFJXNWhZbXhsWkNJNlptRnNjMlVzSW5KMWJuUnBiV1ZGYm5acGNtOXViV1Z1ZENJNmV5SnpaWEoyWlhJaU9tWmhiSE5sTENKa1pYWnBZMlZVYVdWa0lqcG1ZV3h6Wlgwc0ltWmxZWFIxY21WeklqcDdJbUpoYzJVaU9uUnlkV1VzSW5OMFpYSmxiMHhwZG1WdVpYTnpJanAwY25WbExDSmxlSEJ2Y25RaU9uUnlkV1Y5TENKbGJtWnZjbU5sWkZCeVpXWmxjbVZ1WTJWeklqcDdJbTFoYm1SaGRHOXllVXhwZG1WdVpYTnpJanBtWVd4elpYMHNJblpsY25OcGIyNGlPaUkwTGlvdUtpSjkifQ==";
            VeridiumSDK.init(appContext,
                    new DefaultVeridiumSDKModelFactory(appContext),
                    new VeridiumSDKFourExportFInitializer(fourfLicence),
                    new VeridiumSDKDataInitializer()
            );
        } catch (SDKInitializationException e) {
            e.printStackTrace();
        }

        super.onCreate();
    }
}
