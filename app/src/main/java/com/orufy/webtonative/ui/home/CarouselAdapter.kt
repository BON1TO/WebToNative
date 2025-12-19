package com.orufy.webtonative.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.orufy.webtonative.R

class CarouselAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgCarousel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.image.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}
