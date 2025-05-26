package com.app.itaptv

/**
 * @author Arpit Johri
 * @createdOn Saturday, 11th July, 2020
 *
 */

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.interfaces.FragmentChangeListener
import com.app.itaptv.interfaces.NavigationStateListener
import com.app.itaptv.structure.User
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.LocalStorage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_nav_menu.*
import kotlinx.android.synthetic.main.fragment_nav_menu.notification_button
import kotlinx.android.synthetic.main.toolbar.*

class NavigationMenu : Fragment() {


    private lateinit var fragmentChangeListener: FragmentChangeListener
    private lateinit var navigationStateListener: NavigationStateListener

    private var classTag = NavigationMenu::class.java.toString()
    private val home = Constant.nav_menu_home
    private val premium = Constant.nav_menu_premium
    private val more = Constant.nav_menu_more
    private val search = Constant.nav_menu_search
   // private val live = Constant.nav_menu_live
    private val notification = Constant.nav_menu_notification
    var lastSelectedMenu: String? = home
    private var moviesAllowedToGainFocus = false
    private var settingsAllowedToGainFocus = false
    private var podcastsAllowedToGainFocus = false
    private var searchAllowedToGainFocus = true
    private var newsAllowedToGainFocus = false
    private var switchUserAllowedToGainFocus = false
    private var menuTextAnimationDelay = 0//200

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_nav_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //by default selection
        setMenuIconFocusView(R.drawable.ic_home_selected, movies_IB, true)

        //Navigation Menu Options Focus, Key Listeners
        podcastsListeners()
        searchListeners()
        moviesListeners()
        settingsListeners()
        //liveListeners()
        notificationListeners()
        setUserImage()
    }

    private fun moviesListeners() {

        movies_IB.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (isNavigationOpen()) {
                    setFocusedView(movies_IB, R.drawable.ic_home_selected)
                    setMenuNameFocusView(movies_TV, true)
                    focusIn(movies_IB, 0)
                }
            } else {
                if (isNavigationOpen()) {
                    setOutOfFocusedView(movies_IB, R.drawable.ic_home_unselected)
                    setMenuNameFocusView(movies_TV, false)
                    focusOut(movies_IB, 0)
                }
            }
        }

        movies_IB.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {//only when key is pressed down
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        closeNav(lastSelectedMenu!!)
                        navigationStateListener.onStateChanged(false, lastSelectedMenu)
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        lastSelectedMenu = home
                        fragmentChangeListener.switchFragment(home)
                        closeNav(lastSelectedMenu!!)
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (!movies_IB.isFocusable)
                            movies_IB.isFocusable = true
                        switchUserAllowedToGainFocus = true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        lastSelectedMenu = home
                        fragmentChangeListener.switchFragment(home)
                        closeNav(lastSelectedMenu!!)
                    }
                }
            }
            false
        }
    }

    private fun podcastsListeners() {

        podcasts_IB.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (isNavigationOpen()) {
                    setFocusedView(podcasts_IB, R.drawable.ic_premium_selected)
                    setMenuNameFocusView(podcasts_TV, true)
                    focusIn(podcasts_IB, 0)
                }

            } else {
                if (isNavigationOpen()) {
                    setOutOfFocusedView(podcasts_IB, R.drawable.ic_premium_unselected)
                    setMenuNameFocusView(podcasts_TV, false)
                    focusOut(podcasts_IB, 0)
                }
            }


            // Redraw, make the drawing order adjustment of items take effect
            val parent = v.parent as ViewGroup
            parent.requestLayout()
            parent.postInvalidate()
        }

        podcasts_IB.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {//only when key is pressed down
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        closeNav(lastSelectedMenu!!)
                        navigationStateListener.onStateChanged(false, lastSelectedMenu)
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        lastSelectedMenu = premium
                        fragmentChangeListener.switchFragment(premium)
                        closeNav(lastSelectedMenu!!)
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        lastSelectedMenu = premium
                        fragmentChangeListener.switchFragment(premium)
                        closeNav(lastSelectedMenu!!)
                    }
                }
            }
            false
        }
    }

    private fun settingsListeners() {

        settings_IB.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (isNavigationOpen()) {
                    setFocusedView(settings_IB, R.drawable.ic_more_selected)
                    setMenuNameFocusView(settings_TV, true)
                    focusIn(settings_IB, 0)
                }
            } else {
                if (isNavigationOpen()) {
                    setOutOfFocusedView(settings_IB, R.drawable.ic_more_unselected)
                    setMenuNameFocusView(settings_TV, false)
                    focusOut(settings_IB, 0)
                }
            }
        }

        settings_IB.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {//only when key is pressed down
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        closeNav(lastSelectedMenu!!)
                        navigationStateListener.onStateChanged(false, lastSelectedMenu)
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        lastSelectedMenu = more
                        fragmentChangeListener.switchFragment(more)
                        closeNav(lastSelectedMenu!!)
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        lastSelectedMenu = more
                        fragmentChangeListener.switchFragment(more)
                        closeNav(lastSelectedMenu!!)
                    }
                }
            }
            false
        }
    }

    private fun searchListeners() {

        search_IB.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (isNavigationOpen()) {
                    setFocusedView(search_IB, R.drawable.ic_search_selected)
                    setMenuNameFocusView(search_TV, true)
                    focusIn(search_IB, 0)
                }
            } else {
                if (isNavigationOpen()) {
                    setOutOfFocusedView(search_IB, R.drawable.ic_search_unselected)
                    setMenuNameFocusView(search_TV, false)
                    focusOut(search_IB, 0)
                }
            }
        }

        search_IB.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {//only when key is pressed down
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        closeNav(search)
                        navigationStateListener.onStateChanged(false, "")
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        lastSelectedMenu = search
                        fragmentChangeListener.switchFragment(search)
                        closeNav(search)
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        lastSelectedMenu = search
                        fragmentChangeListener.switchFragment(search)
                        closeNav(search)
                    }
                }
            }
            false
        }
    }

 /*   private fun liveListeners() {

        Utility.textFocusListener(live_IB)

        live_IB.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {//only when key is pressed down
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        fragmentChangeListener.switchFragment(live)
                        closeNav("")
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        fragmentChangeListener.switchFragment(live)
                        closeNav("")
                    }
                }
            }
            false
        }
    }*/

    private fun notificationListeners() {
        notification_button.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {//only when key is pressed down
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        closeNav("")
                        navigationStateListener.onStateChanged(false, "")
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        fragmentChangeListener.switchFragment(notification)
                        closeNav("")
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        fragmentChangeListener.switchFragment(notification)
                        closeNav("")
                    }
                }
            }
            false
        }
    }

    private fun setOutOfFocusedView(view: ImageButton, resource: Int) {
        setMenuIconFocusView(resource, view, false)
    }

    private fun setFocusedView(view: ImageButton, resource: Int) {
        setMenuIconFocusView(resource, view, true)
    }


    /**
     * Setting animation when focus is lost
     */
    fun focusOut(v: View, position: Int) {
        val scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.2f, 1f)
        val set = AnimatorSet()
        set.play(scaleX).with(scaleY)
        set.start()
    }

    /**
     * Setting the animation when getting focus
     */
    fun focusIn(v: View, position: Int) {
        val scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.2f)
        val scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.2f)
        val set = AnimatorSet()
        set.play(scaleX).with(scaleY)
        set.start()
    }

    private fun setMenuIconFocusView(resource: Int, view: ImageButton, inFocus: Boolean) {
        view.setImageResource(resource)
    }

    private fun setMenuNameFocusView(view: TextView, inFocus: Boolean) {
        if (inFocus) {
            view.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
        } else
            view.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
    }

    fun openNav() {
        profile_Tv.visibility = View.VISIBLE
        //live_IB.visibility = View.VISIBLE
        notification_button.visibility = View.VISIBLE
        icoins_IB.visibility = View.VISIBLE
        tvUserName.visibility = View.VISIBLE
        icoins_IB.text = HomeActivity.toolbarWalletBalance.text.toString();
        tvUserName.text = LocalStorage.getUserData().displayName;
        enableNavMenuViews(View.VISIBLE)
        navigationStateListener.onStateChanged(true, lastSelectedMenu)
        val scale = requireContext().resources.displayMetrics.density
        val dpWidthInPx: Int = (180 * scale).toInt()
        val lp = FrameLayout.LayoutParams(dpWidthInPx, MATCH_PARENT)
        open_nav_CL.layoutParams = lp

        when (lastSelectedMenu) {

            home -> {
                movies_IB.requestFocus()
                moviesAllowedToGainFocus = true
                setMenuNameFocusView(movies_TV, true)
            }
            premium -> {
                podcasts_IB.requestFocus()
                podcastsAllowedToGainFocus = true
                setMenuNameFocusView(podcasts_TV, true)
            }
            more -> {
                settings_IB.requestFocus()
                settingsAllowedToGainFocus = true
                setMenuNameFocusView(settings_TV, true)
            }
            search -> {
                search_IB.requestFocus()
                searchAllowedToGainFocus = true
                setMenuNameFocusView(search_TV, true)
            }

        }

    }

    fun closeNav(lastSelectedScreen: String) {
        profile_Tv.visibility = View.GONE
        //live_IB.visibility = View.GONE
        notification_button.visibility = View.GONE
        icoins_IB.visibility = View.GONE
        tvUserName.visibility = View.GONE
        enableNavMenuViews(View.GONE)
        if (lastSelectedScreen.isNotEmpty()) {
            navigationStateListener.onStateChanged(false, lastSelectedMenu)
        }
        val lp = FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        open_nav_CL.layoutParams = lp

        //highlighting last selected menu icon
        highlightMenuSelection(lastSelectedMenu)

        //Setting out of focus views for menu icons, names
        unHighlightMenuSelections(lastSelectedMenu)

    }

    private fun unHighlightMenuSelections(lastSelectedMenu: String?) {
        if (!lastSelectedMenu.equals(home, true)) {
            setOutOfFocusedView(movies_IB, R.drawable.ic_home_unselected)
            setMenuNameFocusView(movies_TV, false)
        }
        if (!lastSelectedMenu.equals(premium, true)) {
            setOutOfFocusedView(podcasts_IB, R.drawable.ic_premium_unselected)
            setMenuNameFocusView(podcasts_TV, false)
        }
        if (!lastSelectedMenu.equals(more, true)) {
            setOutOfFocusedView(settings_IB, R.drawable.ic_more_unselected)
            setMenuNameFocusView(settings_TV, false)
        }
        if (!lastSelectedMenu.equals(search, true)) {
            setOutOfFocusedView(search_IB, R.drawable.ic_search_unselected)
            setMenuNameFocusView(search_TV, false)
        }
    }

    private fun highlightMenuSelection(lastSelectedMenu: String?) {
        when (lastSelectedMenu) {
            home -> {
                setFocusedView(movies_IB, R.drawable.ic_home_selected)
            }
            premium -> {
                setFocusedView(podcasts_IB, R.drawable.ic_premium_selected)
            }
            more -> {
                setFocusedView(settings_IB, R.drawable.ic_more_selected)
            }
            search -> {
                setFocusedView(search_IB, R.drawable.ic_search_selected)
            }
        }
    }

    private fun enableNavMenuViews(visibility: Int) {

        if (visibility == View.GONE) {
            menuTextAnimationDelay = 0//200 //reset
            movies_TV.visibility = visibility
            podcasts_TV.visibility = visibility
            settings_TV.visibility = visibility
            search_TV.visibility = visibility
        } else {
            animateMenuNamesEntry(movies_TV, visibility, 1)
        }

    }

    private fun animateMenuNamesEntry(view: View, visibility: Int, viewCode: Int) {
        view.postDelayed({
            view.visibility = visibility
            val animate = AnimationUtils.loadAnimation(context, R.anim.slide_in_left_menu_name)
            view.startAnimation(animate)
            menuTextAnimationDelay = 100
            when (viewCode) {
                1 -> {
                    animateMenuNamesEntry(search_TV, visibility, viewCode + 1)
                }
                2 -> {
                    animateMenuNamesEntry(podcasts_TV, visibility, viewCode + 1)
                }
                3 -> {
                    animateMenuNamesEntry(settings_TV, visibility, viewCode + 1)
                }
            }
        }, menuTextAnimationDelay.toLong())
    }

    fun isNavigationOpen() = movies_TV.visibility == View.VISIBLE

    fun setFragmentChangeListener(callback: FragmentChangeListener) {
        this.fragmentChangeListener = callback
    }

    fun setNavigationStateListener(callback: NavigationStateListener) {
        this.navigationStateListener = callback
    }

    fun setSelectedMenu(navMenuName: String) {
        when (navMenuName) {
            home -> {
                lastSelectedMenu = home
            }
        }

        highlightMenuSelection(lastSelectedMenu)
        unHighlightMenuSelections(lastSelectedMenu)

    }

    private fun setUserImage() {
        val gson = Gson()
        val json = LocalStorage.getValue(
            LocalStorage.KEY_USER_DATA, "",
            String::class.java
        )
        val obj = gson.fromJson(json, User::class.java)
        if (obj != null) {
            loadProfileImage(obj.img)
        }
    }

    private fun loadProfileImage(imageUrl: String) {
        if (activity == null) return
        val gson = Gson()
        val json = LocalStorage.getValue(
            LocalStorage.KEY_USER_DATA, "",
            String::class.java
        )
        val obj = gson.fromJson(json, User::class.java)
        val options = RequestOptions()
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.user)
        if (imageUrl.isNotEmpty()) {
            Glide.with(requireActivity())
                .load(imageUrl)
                .thumbnail(0.1f)
                .apply(options)
                .into(profile_Tv)
        }
    }


}
