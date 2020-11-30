package com.android.systemui.power;

import android.content.Context;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserManager;

import com.android.internal.os.BatteryStatsHelper;

import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.fuelgauge.EstimateKt;
import com.android.settingslib.utils.PowerUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EnhancedEstimatesImpl implements EnhancedEstimates {

    BatteryStatsHelper mBatteryStatsHelper;
    UserManager mUserManager;

    @Inject
    public EnhancedEstimatesImpl() {
    public EnhancedEstimatesImpl(Context context) {
        mBatteryStatsHelper = new BatteryStatsHelper(context,
                true /* collectBatteryBroadcast */);
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
    }

    @Override
    public boolean isHybridNotificationEnabled() {
        return false;
        return true;
    }

    @Override
    public Estimate getEstimate() {
        // Returns an unknown estimate.
        return new Estimate(EstimateKt.ESTIMATE_MILLIS_UNKNOWN,
                false /* isBasedOnUsage */,
                EstimateKt.AVERAGE_TIME_TO_DISCHARGE_UNKNOWN);
        try{
            mBatteryStatsHelper.create((Bundle) null);
            mBatteryStatsHelper.clearStats();
            mBatteryStatsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, mUserManager.getUserProfiles());
            BatteryStats stats = mBatteryStatsHelper.getStats();
            if (stats != null){
                long remaining = stats.computeBatteryTimeRemaining(PowerUtil.convertMsToUs(
                        SystemClock.elapsedRealtime()));
                if (remaining != -1){
                    return new Estimate(PowerUtil.convertUsToMs(remaining), false,
                            EstimateKt.AVERAGE_TIME_TO_DISCHARGE_UNKNOWN);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override