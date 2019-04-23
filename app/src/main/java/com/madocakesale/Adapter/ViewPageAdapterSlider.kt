package com.madocakesale.Adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.madocakesale.Model.Slider
import com.madocakesale.R
import com.squareup.picasso.Picasso

class ViewPageAdapterSlider() : PagerAdapter() {

    private lateinit var slider_list: MutableList<Slider>
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

    constructor(slider_list: MutableList<Slider>, context: Context) : this() {
        this.slider_list = slider_list
        this.context = context
    }


    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view.equals(obj)
    }


    override fun getCount(): Int {
        return slider_list.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        var view: View = layoutInflater.inflate(R.layout.slider_item_slider, container, false)

        var slider_image: ImageView=view.findViewById(R.id.slider_image)
        var slider_text: TextView=view.findViewById(R.id.slider_name)

        Picasso.with(context).load(slider_list.get(position).Slider_image).into(slider_image)
        slider_text.setText(slider_list.get(position).Slider_name)

        container.addView(view,0)

        return view
    }


}






























