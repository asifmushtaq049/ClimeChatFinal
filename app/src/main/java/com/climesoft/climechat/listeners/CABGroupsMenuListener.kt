package com.climesoft.climechat.listeners

/**
 * Created by Asif on 6/10/2017.
 */

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialcab.MaterialCab
import com.climesoft.climechat.R
import com.climesoft.climechat.ConversationActivity
import com.climesoft.climechat.adapters.MainGroupsAdapter
import com.climesoft.climechat.common.CommonUtil
import com.climesoft.climechat.database.DBActions
import com.climesoft.climechat.database.DBInfo
import com.climesoft.climechat.database.DBMeta


class CABGroupsMenuListener(val rootView : RecyclerView, val selectedItems : ArrayList<Int>)
    : View.OnClickListener, View.OnLongClickListener, MaterialCab.Callback {

    var cabMenu = MaterialCab(rootView.context as AppCompatActivity, R.id.cab_stub)
    val context = rootView.context
    val adapter = rootView.adapter as MainGroupsAdapter
    val dbInfo = DBInfo(context)


    override fun onClick(view : View){
        val position : Int = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
        if(!selectedItems.isEmpty()){
            manageSelectedList(position, view)
        }else{
            val data = Bundle()
            data.putLong("groupId", adapter.getItemId(position))
            CommonUtil.goToActivityWithData(context, ConversationActivity::class.java, data)
        }
    }
    override fun onLongClick(view : View) : Boolean{
        val position : Int = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
        manageSelectedList(position, view)
        return true
    }
    private fun manageSelectedList(position : Int, view : View){
        if(selectedItems.contains(position)){
            selectedItems.remove(position)
            updateCabMenu()
        }else{
            selectedItems.add(position)
            updateCabMenu()
        }
        manageSelection(view, position)
    }

    private fun manageSelection(view : View, position : Int){
        view.setSelected(selectedItems.contains(position))
    }

    fun contains(position : Int) : Boolean{
        return selectedItems.contains(position)
    }

    override fun onCabCreated(cab: MaterialCab, menu: Menu): Boolean {
        cab.setMenu(R.menu.cab_menu_main_groups)
        return true
    }

    override fun onCabItemClicked(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete -> deleteSelectedItems()
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
            val groupId = adapter.getItemId(deleteItems.next())
            if(!dbInfo.haveMembers(groupId)){
                dbActions.deleteMembers(groupId)
                dbActions.deleteMessages(groupId)
                dbActions.delete(groupId, "groups")
                deleteItems.remove()
            }else{
                CommonUtil.showMessage(context, "Please leave the group first")
                clearSelection()
                return
            }

        }
        adapter.changeCursor(dbActions.getCursor(DBMeta.FETCH_GROUPS_DESC_ORDER_BY_DATE));
        adapter.notifyDataSetChanged()
        updateCabMenu()
    }
}