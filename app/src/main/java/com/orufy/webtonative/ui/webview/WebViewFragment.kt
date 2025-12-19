package com.orufy.webtonative.ui.webview

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
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

        val url = arguments?.getString("url") ?: return

        // WebView setup
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadUrl(url)
        }

        // Toolbar back button
        binding.webToolbar.setNavigationOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                findNavController().popBackStack()
            }
        }

        // System back button
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
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
