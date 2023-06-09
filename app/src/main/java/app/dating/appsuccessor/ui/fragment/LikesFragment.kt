package app.dating.appsuccessor.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.FragmentLikesBinding
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class LikesFragment : Fragment() {
    private lateinit var ui: FragmentLikesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ui = FragmentLikesBinding.inflate(layoutInflater)


        activity?.setStatusBarColor(Color.parseColor("#F7F9F7"))


        return ui.root
    }

}