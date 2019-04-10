/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aconno.sensorics.adapter.viewpager2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.collection.ArraySet
import androidx.collection.LongSparseArray
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.StatefulAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * Similar in behavior to [FragmentStatePagerAdapter]
 *
 *
 * Lifecycle within [RecyclerView]:
 *
 *  * [RecyclerView.ViewHolder] initially an empty [FrameLayout], serves as a
 * re-usable container for a [Fragment] in later stages.
 *  * [RecyclerView.Adapter.onBindViewHolder] we ask for a [Fragment] for the
 * position. If we already have the fragment, or have previously saved its state, we use those.
 *  * [RecyclerView.Adapter.onAttachedToWindow] we attach the [Fragment] to a
 * container.
 *  * [RecyclerView.Adapter.onViewRecycled] and
 * [RecyclerView.Adapter.onFailedToRecycleView] we remove, save state, destroy the
 * [Fragment].
 *
 */
abstract class FragmentStateAdapter
/**
 * @param fragmentManager of [ViewPager2]'s host
 * @param lifecycle       of [ViewPager2]'s host
 * @see FragmentStateAdapter.FragmentStateAdapter
 * @see FragmentStateAdapter.FragmentStateAdapter
 */
    (
    private val mFragmentManager: FragmentManager,
    private val mLifecycle: Lifecycle
) : RecyclerView.Adapter<FragmentViewHolder>(), StatefulAdapter {

    // Fragment bookkeeping
    private val mFragments = LongSparseArray<Fragment>()
    private val mSavedStates = LongSparseArray<Fragment.SavedState>()
    private val mItemIdToViewHolder = LongSparseArray<Int>()

    // Fragment GC
    internal // to avoid creation of a synthetic accessor
    var mIsInGracePeriod = false
    private var mHasStaleFragments = false

    /**
     * @param fragmentActivity if the [ViewPager2] lives directly in a
     * [FragmentActivity] subclass.
     * @see FragmentStateAdapter.FragmentStateAdapter
     * @see FragmentStateAdapter.FragmentStateAdapter
     */
    constructor(fragmentActivity: FragmentActivity) : this(
        fragmentActivity.supportFragmentManager,
        fragmentActivity.lifecycle
    ) {
    }

    /**
     * @param fragment if the [ViewPager2] lives directly in a [Fragment] subclass.
     * @see FragmentStateAdapter.FragmentStateAdapter
     * @see FragmentStateAdapter.FragmentStateAdapter
     */
    constructor(fragment: Fragment) : this(fragment.childFragmentManager, fragment.lifecycle) {}

    init {
        super.setHasStableIds(true)
    }

    /**
     * Provide a Fragment associated with the specified position.
     */
    abstract fun getItem(position: Int): Fragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentViewHolder {
        return FragmentViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int) {
        val itemId = holder.itemId
        val viewHolderId = holder.container.getId()
        val boundItemId = itemForViewHolder(viewHolderId) // item currently bound to the VH
        if (boundItemId != null && boundItemId != itemId) {
            removeFragment(boundItemId)
            mItemIdToViewHolder.remove(boundItemId)
        }

        mItemIdToViewHolder.put(itemId, viewHolderId) // this might overwrite an existing entry
        ensureFragment(position)

        /** Special case when [RecyclerView] decides to keep the [container]
         * attached to the window, but not to the view hierarchy (i.e. parent is null)  */
        val container = holder.container
        if (ViewCompat.isAttachedToWindow(container)) {
            if (container.parent != null) {
                throw IllegalStateException("Design assumption violated.")
            }
            container.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View, left: Int, top: Int, right: Int, bottom: Int,
                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    if (container.parent != null) {
                        container.removeOnLayoutChangeListener(this)
                        placeFragmentInViewHolder(holder)
                    }
                }
            })
        }

        gcFragments()
    }

    private fun gcFragments() {
        if (!mHasStaleFragments || shouldDelayFragmentTransactions()) {
            return
        }

        // Remove Fragments for items that are no longer part of the data-set
        val toRemove = ArraySet<Long>()
        for (ix in 0 until mFragments.size()) {
            val itemId = mFragments.keyAt(ix)
            if (!containsItem(itemId)) {
                toRemove.add(itemId)
                mItemIdToViewHolder.remove(itemId) // in case they're still bound
            }
        }

        // Remove Fragments that are not bound anywhere -- pending a grace period
        if (!mIsInGracePeriod) {
            mHasStaleFragments = false // we've executed all GC checks

            for (ix in 0 until mFragments.size()) {
                val itemId = mFragments.keyAt(ix)
                if (!mItemIdToViewHolder.containsKey(itemId)) {
                    toRemove.add(itemId)
                }
            }
        }

        for (itemId in toRemove) {
            removeFragment(itemId!!)
        }
    }// to avoid creation of a synthetic accessor

    private fun itemForViewHolder(viewHolderId: Int): Long? {
        var boundItemId: Long? = null
        for (ix in 0 until mItemIdToViewHolder.size()) {
            if (mItemIdToViewHolder.valueAt(ix) == viewHolderId) {
                if (boundItemId != null) {
                    throw IllegalStateException("Design assumption violated: " + "a ViewHolder can only be bound to one item at a time.")
                }
                boundItemId = mItemIdToViewHolder.keyAt(ix)
            }
        }
        return boundItemId
    }

    private fun ensureFragment(position: Int) {
        val itemId = getItemId(position)
        if (!mFragments.containsKey(itemId)) {
            val newFragment = getItem(position)
            newFragment.setInitialSavedState(mSavedStates.get(itemId))
            mFragments.put(itemId, newFragment)
        }
    }

    override fun onViewAttachedToWindow(holder: FragmentViewHolder) {
        placeFragmentInViewHolder(holder)
        gcFragments()
    }

    /**
     * @param holder that has been bound to a Fragment in the [.onBindViewHolder] stage.
     */
    internal fun placeFragmentInViewHolder(holder: FragmentViewHolder) {
        val fragment = mFragments.get(holder.itemId)
            ?: throw IllegalStateException("Design assumption violated.")
        val container = holder.container
        val view = fragment.view

        /*
        possible states:
        - fragment: { added, notAdded }
        - view: { created, notCreated }
        - view: { attached, notAttached }

        combinations:
        - { f:added, v:created, v:attached } -> check if attached to the right container
        - { f:added, v:created, v:notAttached} -> attach view to container
        - { f:added, v:notCreated, v:attached } -> impossible
        - { f:added, v:notCreated, v:notAttached} -> schedule callback for when created
        - { f:notAdded, v:created, v:attached } -> illegal state
        - { f:notAdded, v:created, v:notAttached } -> illegal state
        - { f:notAdded, v:notCreated, v:attached } -> impossible
        - { f:notAdded, v:notCreated, v:notAttached } -> add, create, attach
         */

        // { f:notAdded, v:created, v:attached } -> illegal state
        // { f:notAdded, v:created, v:notAttached } -> illegal state
        if (!fragment.isAdded && view != null) {
            throw IllegalStateException("Design assumption violated.")
        }

        // { f:added, v:notCreated, v:notAttached} -> schedule callback for when created
        if (fragment.isAdded && view == null) {
            scheduleViewAttach(fragment, container)
            return
        }

        // { f:added, v:created, v:attached } -> check if attached to the right container
        if (fragment.isAdded && view!!.parent != null) {
            if (view.parent !== container) {
                addViewToContainer(view, container)
            }

            //Set menu visibility of All Fragments to false
            setMenuVisibilityOfAllFragments(false)
            //Set menu visibility of primary item
            fragment.setMenuVisibility(true)
            return
        }

        // { f:added, v:created, v:notAttached} -> attach view to container
        if (fragment.isAdded) {
            addViewToContainer(view!!, container)
            return
        }

        // { f:notAdded, v:notCreated, v:notAttached } -> add, create, attach
        if (!shouldDelayFragmentTransactions()) {
            scheduleViewAttach(fragment, container)

            //Set menu visibility of All Fragments to false
            setMenuVisibilityOfAllFragments(false)
            //Set menu visibility of primary item
            fragment.setMenuVisibility(true)
            mFragmentManager.beginTransaction().add(fragment, "f" + holder.itemId).commitNow()
        } else {
            if (mFragmentManager.isDestroyed) {
                return  // nothing we can do
            }
            mLifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(
                    source: LifecycleOwner,
                    event: Lifecycle.Event
                ) {
                    if (shouldDelayFragmentTransactions()) {
                        return
                    }
                    source.lifecycle.removeObserver(this)
                    if (ViewCompat.isAttachedToWindow(holder.container)) {
                        placeFragmentInViewHolder(holder)
                    }
                }
            })
        }
    }// to avoid creation of a synthetic accessor

    /**
     * Sets visibility of all existing fragments with visibility param
     * @param visibility
     */
    private fun setMenuVisibilityOfAllFragments(visibility: Boolean) {
        for (i in 0 until mFragments.size()) {
            val key = mFragments.keyAt(i)
            // get the object by the key.
            val obj = mFragments.get(key)
            obj?.setMenuVisibility(visibility!!)
        }
    }

    private fun scheduleViewAttach(fragment: Fragment, container: FrameLayout) {
        // After a config change, Fragments that were in FragmentManager will be recreated. Since
        // ViewHolder container ids are dynamically generated, we opted to manually handle
        // attaching Fragment views to containers. For consistency, we use the same mechanism for
        // all Fragment views.
        mFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment, v: View,
                    savedInstanceState: Bundle?
                ) {
                    if (f === fragment) {
                        fm.unregisterFragmentLifecycleCallbacks(this)
                        addViewToContainer(v, container)
                    }
                }
            }, false
        )
    }

    internal fun addViewToContainer(v: View, container: FrameLayout) {
        if (container.childCount > 1) {
            throw IllegalStateException("Design assumption violated.")
        }

        if (v.parent === container) {
            return
        }

        if (container.childCount > 0) {
            container.removeAllViews()
        }

        if (v.parent != null) {
            (v.parent as ViewGroup).removeView(v)
        }

        container.addView(v)
    }// to avoid creation of a synthetic accessor

    override fun onViewRecycled(holder: FragmentViewHolder) {
        val viewHolderId = holder.container.getId()
        val boundItemId = itemForViewHolder(viewHolderId) // item currently bound to the VH
        if (boundItemId != null) {
            removeFragment(boundItemId)
            mItemIdToViewHolder.remove(boundItemId)
        }
    }

    override fun onFailedToRecycleView(holder: FragmentViewHolder): Boolean {
        // This happens when a ViewHolder is in a transient state (e.g. during custom
        // animation). We don't have sufficient information on how to clear up what lead to
        // the transient state, so we are throwing away the ViewHolder to stay on the
        // conservative side.
        onViewRecycled(holder) // the same clean-up steps as when recycling a ViewHolder
        return false // don't recycle the view
    }

    private fun removeFragment(itemId: Long) {
        val fragment = mFragments.get(itemId) ?: return

        if (fragment.view != null) {
            val viewParent = fragment.view!!.parent
            if (viewParent != null) {
                (viewParent as FrameLayout).removeAllViews()
            }
        }

        if (!containsItem(itemId)) {
            mSavedStates.remove(itemId)
        }

        if (!fragment.isAdded) {
            mFragments.remove(itemId)
            return
        }

        if (shouldDelayFragmentTransactions()) {
            mHasStaleFragments = true
            return
        }

        if (fragment.isAdded && containsItem(itemId)) {
            mSavedStates.put(itemId, mFragmentManager.saveFragmentInstanceState(fragment))
        }
        mFragmentManager.beginTransaction().remove(fragment).commitNow()
        mFragments.remove(itemId)
    }

    internal fun shouldDelayFragmentTransactions():
    // to avoid creation of a synthetic accessor
            Boolean {
        return mFragmentManager.isStateSaved
    }

    /**
     * Default implementation works for collections that don't add, move, remove items.
     *
     *
     * TODO(b/122670460): add lint rule
     * When overriding, also override [.containsItem].
     *
     *
     * If the item is not a part of the collection, return [RecyclerView.NO_ID].
     *
     * @param position Adapter position
     * @return stable item id [RecyclerView.Adapter.hasStableIds]
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Default implementation works for collections that don't add, move, remove items.
     *
     *
     * TODO(b/122670460): add lint rule
     * When overriding, also override [.getItemId]
     */
    fun containsItem(itemId: Long): Boolean {
        return itemId in 0..(itemCount - 1)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        throw UnsupportedOperationException(
            "Stable Ids are required for the adapter to function properly, and the adapter " + "takes care of setting the flag."
        )
    }

    override fun saveState(): Parcelable {
        /** TODO(b/122670461): use custom [Parcelable] instead of Bundle to save space  */
        val savedState = Bundle(mFragments.size() + mSavedStates.size())

        /** save references to active fragments  */
        for (ix in 0 until mFragments.size()) {
            val itemId = mFragments.keyAt(ix)
            val fragment = mFragments.get(itemId)
            if (fragment != null && fragment.isAdded) {
                val key =
                    createKey(
                        KEY_PREFIX_FRAGMENT,
                        itemId
                    )
                mFragmentManager.putFragment(savedState, key, fragment)
            }
        }

        /** Write [) into a ][mSavedStates] */
        for (ix in 0 until mSavedStates.size()) {
            val itemId = mSavedStates.keyAt(ix)
            if (containsItem(itemId)) {
                val key =
                    createKey(
                        KEY_PREFIX_STATE,
                        itemId
                    )
                savedState.putParcelable(key, mSavedStates.get(itemId))
            }
        }

        return savedState
    }

    override fun restoreState(savedState: Parcelable) {
        if (!mSavedStates.isEmpty || !mFragments.isEmpty) {
            throw IllegalStateException(
                "Expected the adapter to be 'fresh' while restoring state."
            )
        }

        val bundle = savedState as Bundle

        for (key in bundle.keySet()) {
            if (isValidKey(
                    key,
                    KEY_PREFIX_FRAGMENT
                )
            ) {
                val itemId =
                    parseIdFromKey(
                        key,
                        KEY_PREFIX_FRAGMENT
                    )
                val fragment = mFragmentManager.getFragment(bundle, key)
                mFragments.put(itemId, fragment)
                continue
            }

            if (isValidKey(
                    key,
                    KEY_PREFIX_STATE
                )
            ) {
                val itemId =
                    parseIdFromKey(
                        key,
                        KEY_PREFIX_STATE
                    )
                val state = bundle.getParcelable<Fragment.SavedState>(key)
                if (containsItem(itemId)) {
                    mSavedStates.put(itemId, state)
                }
                continue
            }

            throw IllegalArgumentException("Unexpected key in savedState: $key")
        }

        if (!mFragments.isEmpty) {
            mHasStaleFragments = true
            mIsInGracePeriod = true
            gcFragments()
            scheduleGracePeriodEnd()
        }
    }

    private fun scheduleGracePeriodEnd() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            mIsInGracePeriod = false
            gcFragments() // good opportunity to GC
        }

        mLifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    handler.removeCallbacks(runnable)
                    source.lifecycle.removeObserver(this)
                }
            }
        })

        handler.postDelayed(
            runnable,
            GRACE_WINDOW_TIME_MS
        )
    }

    companion object {
        // State saving config
        private val KEY_PREFIX_FRAGMENT = "f#"
        private val KEY_PREFIX_STATE = "s#"

        // Fragment GC config
        private val GRACE_WINDOW_TIME_MS: Long = 10000 // 10 seconds

        // Helper function for dealing with save / restore state
        private fun createKey(prefix: String, id: Long): String {
            return prefix + id
        }

        // Helper function for dealing with save / restore state
        private fun isValidKey(key: String, prefix: String): Boolean {
            return key.startsWith(prefix) && key.length > prefix.length
        }

        // Helper function for dealing with save / restore state
        private fun parseIdFromKey(key: String, prefix: String): Long {
            return java.lang.Long.parseLong(key.substring(prefix.length))
        }
    }
}
