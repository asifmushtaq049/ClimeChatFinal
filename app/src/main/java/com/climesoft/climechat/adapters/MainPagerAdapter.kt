package com.climesoft.climechat.adapters

/**
 * Created by Asif on 6/4/2017.
 */
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentManager
import android.support.v4.app.Fragment
import android.content.Context
import com.climesoft.climechat.fragments.GroupsFragment
import com.climesoft.climechat.R
import com.climesoft.climechat.fragments.PointsFragment


class MainPagerAdapter(val fm : FragmentManager, val context : Context) : FragmentPagerAdapter(fm){

    override fun getCount() : Int{
        return 2
    }
    override fun getItem(position : Int) : Fragment? {
        when(position){
            0 -> return GroupsFragment()
            1 -> return PointsFragment()
        }
        return null
    }

    override fun getPageTitle(position : Int) : CharSequence {
        when(position){
            0 -> return context.getResources().getString(R.string.conversations)
            1 -> return context.getResources().getString(R.string.file_share)
        }
        return ""
    }

}