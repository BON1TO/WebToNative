package com.orufy.webtonative.ui.webview

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.orufy.webtonative.R
import com.orufy.webtonative.databinding.FragmentWebviewBinding

class WebViewFragment : Fragment(R.layout.fragment_webview) {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWebviewBinding.bind(view)

        // ✅ APPLY SYSTEM INSETS (FIXES NOTCH / STATUS BAR OVERLAP)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val url = arguments?.getString("url") ?: return

        // ✅ Show URL in the URL bar initially
        binding.tvUrl.text = url

        // 🌐 WebView setup
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, newUrl: String?) {
                    super.onPageFinished(view, newUrl)
                    // ✅ Update URL bar when page changes
                    binding.tvUrl.text = newUrl
                }
            }

            webChromeClient = WebChromeClient()
            loadUrl(url)
        }

        // 🔙 Toolbar back button
        binding.webToolbar.setNavigationOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                findNavController().popBackStack()
            }
        }

        // ❌ Close button
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        // 🔙 System back button
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        isEnabled = false
                        findNavController().popBackStack()
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
