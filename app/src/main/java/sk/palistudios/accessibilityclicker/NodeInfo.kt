package sk.palistudios.accessibilityclicker


import android.graphics.Rect
import android.os.Build
import android.support.v4.view.ViewPager
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.viewpager.widget.ViewPager
import java.util.*


/**
 * Created by chrismcmeeking on 2/25/17.
 */
class NodeInfo : Iterable<NodeInfo?>,
    Comparator<NodeInfo?> {
    companion object {
        fun wrap(node: AccessibilityNodeInfo?): NodeInfo? {
            return node?.let { NodeInfo(it) }
        }

        fun wrap(node: AccessibilityNodeInfoCompat?): NodeInfo? {
            return node?.let { NodeInfo(it) }
        }

        private var ACTIVE_CLASSES: ArrayList<Class<out View?>>? =
            null

        init {
            val ACTIVE_CLASSES =
                ArrayList<Class<out View?>>()
            ACTIVE_CLASSES.add(Button::class.java)
            ACTIVE_CLASSES.add(Switch::class.java)
            ACTIVE_CLASSES.add(CheckBox::class.java)
            ACTIVE_CLASSES.add(EditText::class.java)
        }
    }

    enum class Actions(val androidValue: Int) {
        ACCESSIBILITY_FOCUS(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS), CLEAR_ACCESSIBILITY_FOCUS(
            AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS
        ),
        CLEAR_FOCUS(AccessibilityNodeInfo.ACTION_CLEAR_FOCUS), CLEAR_SELECTION(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION), CLICK(
            AccessibilityNodeInfo.ACTION_CLICK
        ),
        COLLAPSE(AccessibilityNodeInfo.ACTION_COLLAPSE), COPY(AccessibilityNodeInfo.ACTION_COPY), CUT(
            AccessibilityNodeInfo.ACTION_CUT
        ),
        LONG_CLICK(AccessibilityNodeInfo.ACTION_LONG_CLICK), PASTE(AccessibilityNodeInfo.ACTION_PASTE), PREVIOUS_AT_MOVEMENT_GRANULARITY(
            AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY
        ),
        PREVIOUS_HTML_ELEMENT(AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT);

    }

    val accessibilityNodeInfoCompat: AccessibilityNodeInfoCompat?

    //A special constructor for testing.
    protected constructor() {
        accessibilityNodeInfoCompat = null
    }

    protected constructor(nodeInfo: AccessibilityNodeInfo?) : this(
        AccessibilityNodeInfoCompat(
            nodeInfo
        )
    ) {
    }

    protected constructor(nodeInfoCompat: AccessibilityNodeInfoCompat?) {
        if (nodeInfoCompat == null) throw RuntimeException("Wrapping a null node doesn't make sense")
        accessibilityNodeInfoCompat = nodeInfoCompat
    }

    override fun compare(p0: NodeInfo?, p1: NodeInfo?): Int {
        val result: Int
        result = p0?.speakableText?.compareTo(p1?.speakableText?:"")?: return -1
        if (result != 0) return result
        val lhsRect = p0?.boundsInScreen
        val rhsRect = p1?.boundsInScreen
        if (result != 0) return result
        if (lhsRect.top < rhsRect!!.top) return -1 else if (lhsRect.top > rhsRect.top) return 1
        if (lhsRect.left < rhsRect!!.left) return -1 else if (lhsRect.left > rhsRect.left) return 1
        if (lhsRect.right < rhsRect!!.right) return -1 else if (lhsRect.right > rhsRect.right) return 1
        if (lhsRect.bottom < rhsRect!!.bottom) return -1 else if (lhsRect.bottom > rhsRect.bottom) return 1
        return if (result != 0) result else 0
    }

    val actionList: List<AccessibilityNodeInfoCompat.AccessibilityActionCompat>
        get() = accessibilityNodeInfoCompat!!.actionList

    val actions: Int
        get() = accessibilityNodeInfoCompat!!.actions

    /**
     * Callbacks for iterating over the NodeInfo heirarchy.
     */
    interface OnVisitListener {
        /**
         * Called for every node during heirarchy traversals.
         * @param nodeInfo The node that work will be doneon.
         * @return Return true to stop traversing, false to continue.
         */
        fun onVisit(nodeInfo: NodeInfo?): Boolean
    }

    val isActiveElement: Boolean
        get() {
            for (clazz in ACTIVE_CLASSES!!) {
                if (className.equals(clazz.name, ignoreCase = true)) return true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)) return true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK)) return true
                }
                if (actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK)) return true
                if (actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SELECT)) return true
            }
            val actions = actions
            return actions and AccessibilityNodeInfo.ACTION_CLICK != 0 || actions and AccessibilityNodeInfo.ACTION_LONG_CLICK != 0 || actions and AccessibilityNodeInfo.ACTION_SELECT != 0
        }

    fun performAction(action: Actions): Boolean {
        return accessibilityNodeInfoCompat!!.performAction(action.androidValue)
    }

    val boundsInScreen: Rect
        get() {
            val result = Rect()
            accessibilityNodeInfoCompat!!.getBoundsInScreen(result)
            return result
        }

    fun getChild(i: Int): NodeInfo {
        if (i >= accessibilityNodeInfoCompat!!.childCount) throw IndexOutOfBoundsException()
        return NodeInfo(accessibilityNodeInfoCompat.getChild(i))
    }

    val childCount: Int
        get() = accessibilityNodeInfoCompat!!.childCount

    val className: String
        get() = accessibilityNodeInfoCompat!!.className.toString()

    val contentDescription: CharSequence?
        get() = accessibilityNodeInfoCompat!!.contentDescription

    /**
     * I don't often use CharSequence's, and prefer strings.  Note: null strings will return as empty strings!
     * @return The content descriptiong as a NotNull String.
     */
    val contentDescriptionAsString: String
        get() = if (accessibilityNodeInfoCompat!!.contentDescription == null) "" else accessibilityNodeInfoCompat.contentDescription.toString()

    /**
     * Gets the depth of the child in the node info heirarchy.
     * @return The depth of the node.
     */
    val depthInTree: Int
        get() {
            var result = 0
            var parentNode = parent
            while (parentNode != null) {
                parentNode = parentNode.parent
                result++
            }
            return result
        }

    val labeledBy: NodeInfo?
        get() = wrap(accessibilityNodeInfoCompat!!.labeledBy)

    val parent: NodeInfo?
        get() = if (accessibilityNodeInfoCompat!!.parent == null) null else NodeInfo(
            accessibilityNodeInfoCompat.parent
        )//Todo: Use Eyes Free project???  Or make this more advanced.

    /**
     * Attempts to calculate the string that will be read off by TalkBack for a given
     * accessibility node.  Eventually including role, trait, and value information.
     * If null, returns an empty string instead.
     *
     * @return The string representing the spoken text.
     */
    val speakableText: String
        get() {
            //Todo: Use Eyes Free project???  Or make this more advanced.
            if (contentDescription != null) return contentDescriptionAsString
            return if (text != null) textAsString else ""
        }

    val text: CharSequence?
        get() = accessibilityNodeInfoCompat!!.text

    /**
     * Don't like CharSequences, and random null string checks.  This will get the Text
     * as a NotNull String.
     * @return The text as a NotNull String.
     */
    val textAsString: String
        get() = if (text != null) text.toString() else ""

    val viewIdResourceName: String
        get() = if (accessibilityNodeInfoCompat!!.viewIdResourceName == null) "" else accessibilityNodeInfoCompat.viewIdResourceName

    /**
     * Implenting the iterable interface to more easily navigate the node infos children.
     * @return An itarator over the children of this NodeInfo.
     */
    override fun iterator(): MutableIterator<NodeInfo?> {
        return object : MutableIterator<NodeInfo?> {
            private var mNextIndex = 0
            override fun hasNext(): Boolean {
                //ChildCount isn't always accurate.  Nodes may get recycled depending on the vent.
                //So we check the child count AND that the child isn't null.
                return mNextIndex < childCount && (accessibilityNodeInfoCompat == null || accessibilityNodeInfoCompat.getChild(
                    mNextIndex
                ) != null)
            }

            override fun next(): NodeInfo {
                return getChild(mNextIndex++)
            }

            override fun remove() {}
        }
    }

    /**
     * Get the entire node heirarchy as a string.
     * @return The node heirarchy.
     */
    fun toViewHeirarchy(): String {
        val result = StringBuilder()
        result.append("--------------- Accessibility Node Hierarchy ---------------\n")
        visitNodes(object : OnVisitListener {
            override fun onVisit(nodeInfo: NodeInfo?): Boolean {
                for (i in 0 until nodeInfo!!.depthInTree) {
                    result.append('-')
                }
                result.append(nodeInfo.toString())
                result.append('\n')
                return false
            }
        })
        result.append("--------------- Accessibility Node Hierarchy ---------------")
        return result.toString()
    }

    /**
     * Get the first [node][NodeInfo] that matches the given [matcher][NodeInfoMatcher]
     * @param matcher The matcher with props to match.
     * @return The first node that matches.
     */
    fun getFirstNodeThatMatches(matcher: NodeInfoMatcher): NodeInfo? {
        return visitNodes(object : OnVisitListener {
            override fun onVisit(nodeInfo: NodeInfo?): Boolean {
                return matcher.match(nodeInfo)
            }
        })
    }

    fun isClassType(clazz: Class<*>): Boolean {
        return clazz.name.equals(className, ignoreCase = true)
    }

    val isScrollable: Boolean
        get() = accessibilityNodeInfoCompat!!.isScrollable

    val isVisibleToUser: Boolean
        get() = accessibilityNodeInfoCompat!!.isVisibleToUser

    val isInVisibleScrollableField: Boolean
        get() {
            var tempNode = wrap(accessibilityNodeInfoCompat)
            var scrollableView: NodeInfo? = null
            while (tempNode!!.parent != null) {
                if (tempNode.isScrollable && !tempNode.isClassType(ViewPager::class.java)) {
                    scrollableView = tempNode
                }
                tempNode = tempNode.parent
            }
            return scrollableView != null && scrollableView.isVisibleToUser
        }

    override fun toString(): String {
        if (accessibilityNodeInfoCompat == null) throw RuntimeException("This shouldn't be null")
        return accessibilityNodeInfoCompat.toString()
    }

    /**
     * Loop over children in the node heirarchy, until one of them returns true.  Return the
     * first element where "onVisit" returns true.  This can be used to create a very
     * simple "find first" type of method.  Though most of the time, you likely want
     * to travel all, in which case, just return "false" from your onVisit method, and
     * you will visit every node.
     * @param onVisitListener [onVisit][NodeInfo.OnVisitListener.onVisit]
     * will be alled for every node, until [onVisit][NodeInfo.OnVisitListener.onVisit]
     * returns true.
     * @return The first node for which [onVisit][NodeInfo.OnVisitListener.onVisit]  returns true.
     */
    fun visitNodes(onVisitListener: OnVisitListener): NodeInfo? {
        if (onVisitListener.onVisit(this)) return this
        for (child in this) {
            val result = child?.visitNodes(onVisitListener)
            if (result != null) return result
        }
        return null
    }
}