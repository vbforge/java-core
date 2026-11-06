package com.vbforge.practice.practice_3_access_levels_overview;

import com.vbforge.practice.practice_3_access_levels_overview.AccessLevelsOverviewDemo;

/**
 * A subclass in its OWN top-level class (not nested), so the access rules apply
 * exactly as they would for any other class: protected members are visible,
 * private ones are not.
 */
class AuditedProcessor extends AccessLevelsOverviewDemo {

    @Override
    protected void onProcess() {
        System.out.println("Audited processing, counter=" + protectedCounter);   // protected: OK
        // System.out.println(secretKey);   // compile error: secretKey has private access
    }
}
