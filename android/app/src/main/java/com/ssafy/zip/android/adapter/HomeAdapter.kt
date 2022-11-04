package com.ssafy.zip.android.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.zip.android.CustomDialog
import com.ssafy.zip.android.R
import com.ssafy.zip.android.data.FamilyMember
import de.hdodenhof.circleimageview.CircleImageView


class HomeAdapter(private val homeList:ArrayList<FamilyMember>, private val familyName:String, fragmentManager: FragmentManager) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    private var mFragmentManager : FragmentManager

    init {
        mFragmentManager = fragmentManager
    }
    
    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val homeImage: CircleImageView = itemView.findViewById(R.id.home_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        return HomeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val homeImage = homeList[position]
        if(homeImage.profileImg==null) {
            holder.homeImage.setImageResource(R.drawable.ex)
        } else{
            Glide.with(holder.itemView)
                .load(homeImage.profileImg)
                .into(holder.homeImage)
        }

        holder.homeImage.setOnClickListener {
            val dialog = CustomDialog()
            val args = Bundle()
            args.putParcelable("key", homeImage)
            args.putString("familyName", familyName)
            dialog.arguments = args
            dialog.show(mFragmentManager, "됐다")
        }

    }

    override fun getItemCount(): Int {
        return homeList.size
    }

}