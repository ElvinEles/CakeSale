package com.madocakesale.Other

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.madocakesale.MainActivity
import com.madocakesale.R
import io.paperdb.Paper

class BottomExitView : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Paper.init(activity)

        var view:View=inflater.inflate(R.layout.layout_bottom_nav_exit,container,false)

        var exit_app_button:Button=view.findViewById(R.id.exit_app_button)
        var denyproduct_exit:Button=view.findViewById(R.id.denyproduct_exit)

        exit_app_button.setOnClickListener {
            if (Paper.book().read("USER_NAME",String) != null &&  Paper.book().read("USER_PASSWORD",String)!= null ) {
                Paper.book().delete("USER_NAME")
                Paper.book().delete("USER_PASSWORD")
            }
            startActivity(Intent(activity,MainActivity::class.java))
        }

        denyproduct_exit.setOnClickListener {
            this.dismiss()
        }

        return view
    }
}