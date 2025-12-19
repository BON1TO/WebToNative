package com.orufy.webtonative.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.orufy.webtonative.R
import com.orufy.webtonative.databinding.FragmentHistoryBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)

        val prefs = requireContext()
            .getSharedPreferences("history_prefs", 0)

        // 🔙 Back button
        binding.historyToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Load history
        val historyList = prefs.getStringSet("urls", emptySet())!!
            .toList()
            .reversed()
            .toMutableList()

        val adapter = HistoryAdapter(historyList) { url ->
            val bundle = Bundle().apply {
                putString("url", url)
            }
            findNavController().navigate(
                R.id.action_historyFragment_to_webViewFragment,
                bundle
            )
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // ☰ Toolbar menu (SINGLE listener)
        binding.historyToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.action_clear -> {
                    prefs.edit().clear().apply()
                    historyList.clear()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.action_upload -> {
                    uploadHistory(prefs)
                    true
                }

                else -> false
            }
        }

    }

    // ⬆ Upload history to Beeceptor
    private fun uploadHistory(prefs: android.content.SharedPreferences) {
        val history = prefs.getStringSet("urls", emptySet()) ?: emptySet()

        if (history.isEmpty()) {
            Toast.makeText(requireContext(), "No history to upload", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val url = URL("https://orufy-project.free.beeceptor.com/history")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val json = JSONObject().apply {
                    put("urls", JSONArray(history.toList()))
                }

                connection.outputStream.use {
                    it.write(json.toString().toByteArray())
                }

                val success = connection.responseCode in 200..299

                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        if (success) "History uploaded successfully" else "Upload failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                connection.disconnect()

            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Upload error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
