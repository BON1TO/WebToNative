package com.orufy.webtonative.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.orufy.webtonative.R
import com.orufy.webtonative.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // ✅ THIS WAS MISSING (VERY IMPORTANT)
        binding.topAppBar.inflateMenu(R.menu.menu_home)

        // Black overflow dots
        binding.topAppBar.overflowIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_overflow_black)

        // History menu click
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_history -> {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_historyFragment
                    )
                    true
                }
                else -> false
            }
        }

        // Entry animation
        binding.contentCard.startAnimation(
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        )

        setupCarousel()

        // Open website
        binding.btnOpen.setOnClickListener {
            val inputUrl = binding.etUrl.text.toString().trim()

            if (inputUrl.isEmpty()) {
                binding.etUrl.error = "Enter a website URL"
                return@setOnClickListener
            }

            val finalUrl =
                if (inputUrl.startsWith("http://") || inputUrl.startsWith("https://")) {
                    inputUrl
                } else {
                    "https://$inputUrl"
                }

            // Save to history
            saveToHistory(finalUrl)

            val bundle = Bundle().apply {
                putString("url", finalUrl)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_webViewFragment,
                bundle
            )
        }

        // Exit app
        binding.btnExit.setOnClickListener {
            requireActivity().finishAffinity()
        }
    }

    // ------------------ CAROUSEL ------------------

    private fun setupCarousel() {
        val images = listOf(
            R.drawable.carousel_1,
            R.drawable.carousel_2,
            R.drawable.carousel_3
        )

        val adapter = CarouselAdapter(images)
        binding.viewPager.adapter = adapter

        setupDots(images.size)

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateDots(position)
                }
            }
        )
    }

    // ------------------ HISTORY ------------------

    private fun saveToHistory(url: String) {
        val prefs = requireContext()
            .getSharedPreferences("history_prefs", 0)

        val history = prefs.getStringSet("urls", mutableSetOf())!!.toMutableSet()
        history.add(url)

        prefs.edit().putStringSet("urls", history).apply()
    }

    // ------------------ DOTS ------------------

    private fun setupDots(count: Int) {
        binding.dotsLayout.removeAllViews()

        repeat(count) { index ->
            val dot = ImageView(requireContext()).apply {
                setImageResource(
                    if (index == 0) R.drawable.dot_active
                    else R.drawable.dot_inactive
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                layoutParams = params
            }
            binding.dotsLayout.addView(dot)
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until binding.dotsLayout.childCount) {
            val dot = binding.dotsLayout.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == position) R.drawable.dot_active
                else R.drawable.dot_inactive
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
