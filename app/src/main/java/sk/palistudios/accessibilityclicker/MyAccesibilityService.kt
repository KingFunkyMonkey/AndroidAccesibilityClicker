package sk.palistudios.accessibilityclicker

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent




class MyAccesibilityService() : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("accesibilityService", event.toString())
        when (event.eventType) {
            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                Log.d("accesibilityService",NodeInfo.wrap(getRootInActiveWindow())!!.toViewHeirarchy())
                run {
                    //If the event has a source, let's print it out separately.
                    if (event.source != null) {
                        Log.d("accesibilityService", NodeInfo.wrap(event.source)!!.toViewHeirarchy())
                    }
                }
            }
            else -> {
                if (event.source != null) {
                    Log.d("accesibilityService", NodeInfo.wrap(event.source)!!.toViewHeirarchy())
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.e("accesibilityService", "Service Interrupted: Have never actually had this happen.")
    }
}