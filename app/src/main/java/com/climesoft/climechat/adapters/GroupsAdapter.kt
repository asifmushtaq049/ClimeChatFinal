package com.climesoft.climechat.adapters


import android.support.v7.widget.RecyclerView;
import android.content.Context;
import java.util.ArrayList
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import com.climesoft.climechat.ConversationActivity
import com.climesoft.climechat.model.Group
import com.climesoft.climechat.R
import com.climesoft.climechat.common.CommonUtil

class GroupsAdapter(val context : Context, val list : ArrayList<Group>) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val title : TextView = view.findViewById(R.id.groupNameTitle)
        val msg : TextView = view.findViewById(R.id.groupLastMessage)

        init{
            view.setOnClickListener(object : View.OnClickListener{
              override fun onClick(view : View){
                  CommonUtil.goToActivity(view.context, ConversationActivity::class.java)
              }
            })
        }
    }

    override fun getItemCount() : Int{
        return list.count()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(viewGroup : ViewGroup, type : Int) : GroupsAdapter.ViewHolder{
        val view = LayoutInflater.from(context).inflate(R.layout.row_groups_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : GroupsAdapter.ViewHolder, position : Int){
        val group = list.get(position)
        holder.title.text = group.name;
        holder.msg.text = group.lastMessage
    }
}