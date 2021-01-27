package sk.palistudios.accessibilityclicker

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class MyAccesibilityService() : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("accesibilityService", event.toString())
        when (event.eventType) {
            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                Log.d(
                    "accesibilityService",
                    NodeInfo.wrap(getRootInActiveWindow())!!.toViewHeirarchy()
                )
                run {
                    //If the event has a source, let's print it out separately.
                    if (event.source != null) {
                        Log.d(
                            "accesibilityService",
                            NodeInfo.wrap(event.source)!!.toViewHeirarchy()
                        )
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                Toast.makeText(applicationContext, "heya, clicked", Toast.LENGTH_LONG).show()

            }
            else -> {
                if (event.source != null) {
                    Log.d("accesibilityService", NodeInfo.wrap(event.source)!!.toViewHeirarchy())
                }
            }
        }
        tryClickOnFbButton(event)
    }

    fun tryClickOnFbButton(event: AccessibilityEvent) {
        if (rootInActiveWindow != null && event.packageName == "com.facebook.katana") {
            val fbRootLinearLayout = rootInActiveWindow.getChild(0)
            for (i in 0 until fbRootLinearLayout.childCount) {
                    val text = fbRootLinearLayout.getChild(i).text as String? ?: "null"
                    Log.d(
                        "accesibility, text:",
                        (text)
                    )
                    if (text == "More languages…") {
                        fbRootLinearLayout.getChild(i)
                            .performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
            }
        }
    }

    override fun onInterrupt() {
        Log.e("accesibilityService", "Service Interrupted: Have never actually had this happen.")
    }
}