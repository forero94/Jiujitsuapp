package com.sukata.bjj.ui.cronogramas

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.sukata.bjj.R
import java.util.Calendar
import androidx.core.content.ContextCompat

class CronogramaExpandableListAdapter(
    private val context: Context,
    private val listDataHeader: List<String>,            // Meses
    private val listDataChild: HashMap<String, List<String>> // Semanas por mes
) : BaseExpandableListAdapter() {

    // Obtener mes y semana actuales
    private val calendar = Calendar.getInstance()
    private val currentMonthName: String
    private val currentWeekName: String

    init {
        // Obtiene el nombre del mes actual
        val monthIndex = calendar.get(Calendar.MONTH)  // 0-based
        currentMonthName = listDataHeader.getOrNull(monthIndex) ?: ""

        // Determinar semana actual (asumiendo "Semana 1", "Semana 2", ...)
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        currentWeekName = "Semana $weekOfMonth"
    }

    override fun getGroupCount(): Int = listDataHeader.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val mes = listDataHeader[groupPosition]
        return listDataChild[mes]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any = listDataHeader[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val mes = listDataHeader[groupPosition]
        return listDataChild[mes]?.get(childPosition) ?: Pair("", "")
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View {
        val mes = getGroup(groupPosition) as String
        // Usamos elvis operator para asegurar que 'view' no sea nulo
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_month, parent, false)
        val headerTitle = view.findViewById<TextView>(R.id.listHeader)
        headerTitle.text = mes

        // Cambia el fondo a verde si es el mes actual, o restaura el color predeterminado
        if (mes == currentMonthName) {
            headerTitle.setBackgroundColor(Color.GREEN)
        } else {
            headerTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.amarillop))
            // o el color de fondo por defecto
        }

        return view
    }



    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        val semana = getChild(groupPosition, childPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_week, parent, false)
        val txtListChild = view.findViewById<TextView>(R.id.listItem)
        txtListChild.text = semana

        val mes = getGroup(groupPosition) as String
        // Cambia el fondo a verde si es la semana actual dentro del mes actual, o restaura el color predeterminado
        if (mes == currentMonthName && semana == currentWeekName) {
            txtListChild.setBackgroundColor(Color.GREEN)
        } else {
            txtListChild.setBackgroundColor(ContextCompat.getColor(context, R.color.cielo))  // o el color de fondo por defecto
        }

        return view
    }



    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
