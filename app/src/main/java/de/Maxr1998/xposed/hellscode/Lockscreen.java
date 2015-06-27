package de.Maxr1998.xposed.hellscode;

import java.io.BufferedReader;
import java.io.FileReader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import eu.chainfire.libsuperuser.Shell;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Lockscreen implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private static final String KERNEL_CODE_FILE = "/sys/devices/virtual/input/lge_touch/hells_code";
    private static XSharedPreferences PREFS;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        PREFS = new XSharedPreferences(Lockscreen.class.getPackage().getName(), "prefs");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.android.systemui")) {
            return;
        }
        PREFS.makeWorldReadable();

        findAndHookMethod("com.android.systemui.keyguard.KeyguardViewMediator", loadPackageParam.classLoader, "onScreenTurnedOn",
                XposedHelpers.findClass("com.android.internal.policy.IKeyguardShowCallback", loadPackageParam.classLoader), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        PREFS.reload();
                        String pattern;
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(KERNEL_CODE_FILE));
                            pattern = reader.readLine();
                            reader.close();
                        } catch (Exception e) {
                            XposedBridge.log(e.getCause());
                            return;
                        }
                        if (Helpers.shaHash(pattern.trim()).equals(PREFS.getString("KEY", "null"))) {
                            XposedHelpers.callMethod(param.thisObject, "keyguardDone", true, true);
                        }
                        try {
                            Shell.run("su", new String[]{"echo '0' > \"" + KERNEL_CODE_FILE + "\""}, null, false);
                        } catch (Exception e) {
                            XposedBridge.log(e.getCause());
                        }
                    }
                });
    }
}
