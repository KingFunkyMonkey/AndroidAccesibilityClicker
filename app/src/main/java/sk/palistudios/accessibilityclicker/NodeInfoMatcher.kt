package sk.palistudios.accessibilityclicker

import android.graphics.Rect
import android.support.v4.view.ViewPager
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.View


/**
 * Created by chrismcmeeking on 2/25/17.
 */
class NodeInfoMatcher  {

    private var mContentDescription: String? = null
    private var mText: String? = null
    private var mClass: Class<out View?>? = null

    private var mContainedIn: Rect? = null
    private var mPositionEqual: Rect? = null

    private var mViewIdResourceName = ""

    fun NodeInfoMatcher() {}

    fun setContentDescription(contentDescription: String?): NodeInfoMatcher? {
        mContentDescription = contentDescription
        return this
    }

    fun setClass(clazz: Class<out View?>?): NodeInfoMatcher? {
        mClass = clazz
        return this
    }

    fun setPositionContainedIn(rect: Rect?): NodeInfoMatcher? {
        mContainedIn = rect
        return this
    }

    fun setPositionEqualTo(rect: Rect?): NodeInfoMatcher? {
        mPositionEqual = rect
        return this
    }

    fun setText(text: String?): NodeInfoMatcher? {
        mText = text
        return this
    }

    fun setViewIdResourceName(viewIdResourceName: String): NodeInfoMatcher? {
        mViewIdResourceName = viewIdResourceName
        return this
    }

    fun match(nodeInfo: NodeInfo): Boolean {
        val position: Rect = nodeInfo.getBoundsInScreen()
        if (mContainedIn != null) {
            if (position.top < mContainedIn!!.top) return false
            if (position.left < mContainedIn!!.left) return false
            if (position.right > mContainedIn!!.right) return false
            if (position.bottom > mContainedIn!!.bottom) return false
        }
        if (mPositionEqual != null) {
            if (position.top != mPositionEqual!!.top) return false
            if (position.bottom != mPositionEqual!!.bottom) return false
            if (position.left != mPositionEqual!!.left) return false
            if (position.right != mPositionEqual!!.right) return false
        }
        if (mContentDescription != null &&
            (nodeInfo.getContentDescription() == null
                    || !mContentDescription!!.contentEquals(nodeInfo.getContentDescription()))
        ) return false
        if (mText != null && (nodeInfo.getText() == null || !mText!!.contentEquals(nodeInfo.getText()))) return false
        if (mClass != null && !mClass!!.name.contentEquals(nodeInfo.getClassName())) return false
        return if (!nodeInfo.getViewIdResourceName().contains(mViewIdResourceName)) false else true
    }
}