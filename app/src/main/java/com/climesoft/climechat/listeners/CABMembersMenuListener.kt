package com.climesoft.climechat.listeners

/**
 * Created by Asif on 6/10/2017.
 */

import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.support.v7.widget.RecyclerView
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.afollestad.materialcab.MaterialCab
import com.climesoft.climechat.MembersActivity
import com.climesoft.climechat.R
import com.climesoft.climechat.adapters.MembersAdapter
import com.climesoft.climechat.common.CommonUtil
import com.climesoft.climechat.common.SmsUtil
import com.climesoft.climechat.database.DBActions
import com.climesoft.climechat.database.DBInfo
import com.climesoft.climechat.database.DBMeta


class CABMembersMenuListener(val rootView : RecyclerView, val selectedItems : ArrayList<String>, val groupId : Long)
    : View.OnClickListener, View.OnLongClickListener, MaterialCab.Callback {

    var cabMenu = MaterialCab(rootView.context as AppCompatActivity, R.id.cab_stub)
    val context = rootView.context
    val adapter = rootView.adapter as MembersAdapter
    val cursor = adapter.getCursor();
    val dbInfo = DBInfo(context);

    override fun onClick(view : View){
        val position : Int = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
        val phone : String = getPhoneByPosition(position);
        if(!selectedItems.isEmpty()){
            manageSelectedList(phone, view)
        }
    }
    override fun onLongClick(view : View) : Boolean{
        val position : Int = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
        val phone : String = getPhoneByPosition(position);
        manageSelectedList(phone, view)
        return true
    }
    private fun manageSelectedList(phone : String, view : View){
        if(selectedItems.contains(phone)){
            selectedItems.remove(phone)
            updateCabMenu()
        }else{
            if(selectedItems.size < 1){
                selectedItems.add(phone)
                updateCabMenu()
            }
        }
        manageSelection(view, phone)
    }
    private fun manageSelection(view : View, phone : String){
        view.setSelected(selectedItems.contains(phone))
    }

    fun getPhoneByPosition(position : Int) : String{
        return dbInfo.getPhoneByPosition(position, groupId);
    }

    fun contains(phone : String) : Boolean{
        return selectedItems.contains(phone)
    }

    override fun onCabCreated(cab: MaterialCab, menu: Menu): Boolean {
        cab.setMenu(R.menu.cab_menu_main_groups)
        return true
    }

    override fun onCabItemClicked(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete -> {
                val pBar = (context as MembersActivity).findViewById<ProgressBar>(R.id.member_deleting) as ProgressBar
                pBar.visibility = View.VISIBLE
                deleteSelectedItems()
            }
        }
        return true
    }

    override fun onCabFinished(cab: MaterialCab): Boolean {
        clearSelection()
        return true
    }

    private fun updateCabMenu(){
        if(!cabMenu.isActive()){
            cabMenu.start(this);
        }
        if(selectedItems.isEmpty() && cabMenu.isActive()){
            cabMenu.finish();
        }
        cabMenu.setTitle("${selectedItems.size} item(s) Selected")
    }
    private fun clearSelection(){
        selectedItems.clear();
        rootView.adapter.notifyDataSetChanged();
    }

    private fun deleteSelectedItems(){
        val dbActions = DBActions(context)
        val deleteItems = selectedItems.iterator()
        while(deleteItems.hasNext()){
            val phoneNumber = deleteItems.next()
            sendToAll(phoneNumber)
            deleteItems.remove()
        }
        adapter.changeCursor(dbActions.getCursor(DBMeta.FETCH_MEMBERS_OF_GROUP + groupId));
        adapter.notifyDataSetChanged()
        updateCabMenu()
    }
    private fun sendToAll(phoneNumber : String){
        val smsManager = SmsManager.getDefault()
        val members = dbInfo.getGroupMembers(groupId)
        val intent = Intent(MembersActivity.MEMBER_REMOVE_FILTER)
        intent.putExtra("groupId", groupId)
        intent.putExtra("phoneNumber", phoneNumber)
        val removeString = SmsUtil.REMOVE_MEMBER_STRING + groupId+ "/" + phoneNumber;
        var pInt = 3;
        if (members.moveToFirst()) {
            do {
                val phone = members.getString(2)
                val mid = members.getLong(0)
                intent.putExtra("mid", mid)
                val removePI = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                if(!CommonUtil.compareNumbers(phone, phoneNumber)){
                    intent.putExtra("from", 1)
                    smsManager.sendTextMessage(phone, null, removeString, removePI, null)
                }
            } while (members.moveToNext())
            intent.putExtra("from", 2)
            val removePI = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            smsManager.sendTextMessage(phoneNumber,null,  SmsUtil.YOU_ARE_OUT_STRING + groupId, removePI, null);
        }
    }
}