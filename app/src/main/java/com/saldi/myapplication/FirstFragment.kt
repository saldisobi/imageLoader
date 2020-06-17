package com.saldi.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.saldi.myapplication.imageloader.ImageLoader

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
           // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

            var imageView = view.findViewById<ImageView>(R.id.image1)
            activity?.application?.let {
                ImageLoader.getInstance(it).loadImage(
                    "https://img.theweek.in/content/dam/week/news/india/images/2020/5/8/b-ro-.jpg",
                    imageView
                )
            }
        }

        var imageView = view.findViewById<ImageView>(R.id.image)
        activity?.application?.let {
            ImageLoader.getInstance(it).loadImage(
                "https://img.theweek.in/content/dam/week/news/india/images/2020/5/8/b-ro-.jpg",
                imageView
            )
        }
    }
}