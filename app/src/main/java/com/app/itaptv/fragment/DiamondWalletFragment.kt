package com.app.itaptv.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.itaptv.R

class DiamondWalletFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diamond_wallet, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(): DiamondWalletFragment {
            return DiamondWalletFragment()
        }
    }
}