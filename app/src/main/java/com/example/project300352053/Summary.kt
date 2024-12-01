package com.example.project300352053

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.project300352053.data.Entry
import com.example.project300352053.data.EntryDao

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView



import com.example.project300352053.data.EntryOb
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext




@Composable
fun Summary(navController: NavHostController, entryDao: EntryDao) {
    var entries by remember { mutableStateOf(emptyList<Entry>()) }
    var isLoading by remember { mutableStateOf(true) }





    LaunchedEffect(entryDao) {
        val data = withContext(Dispatchers.IO) {

            entryDao.getAll()
        }
        entries=data
        isLoading = false

    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {


        val organizedData = organizeData(entries)

        var EachMonth=HashSet<String>()
        //get the unique months based on the year
        for (entry in entries) {
            var Month: String = "${entry.dateTime.split("-")[0]}-${entry.dateTime.split("-")[1]}"
            EachMonth.add(Month)

        }

        LazyColumn {
            //for each month make a bar graph
            for (month in EachMonth) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    Card {
                        Spacer(modifier = Modifier.height(20.dp))
                        AddDefaultStackedBarChart(organizedData, month)
                    }
                }
            }
        }



        }







}


fun addTypes(data: List<EntryOb>, Month:String):List<Float> {
    var totalled= listOf<Float>()


        var foodGroceries = 0f
        var utility = 0f
        var recreation = 0f
        var transportation = 0f
        var insuranceHealthcare = 0f
        var savingsInvestments = 0f
        var personalCare = 0f
        var entertainmentLeisure = 0f
        var miscellaneous = 0f

        for (entry in data) {
            var entryMonth: String = "${entry.dateTime.split("-")[0]}-${entry.dateTime.split("-")[1]}"
            if(entryMonth==Month) {
                when (entry.type) {
                    "Food/Groceries" -> foodGroceries += entry.amount.toFloat()
                    "Utility" -> utility += entry.amount.toFloat()
                    "Recreation" -> recreation += entry.amount.toFloat()
                    "Transportation" -> transportation += entry.amount.toFloat()
                    "Insurance and Healthcare" -> insuranceHealthcare += entry.amount.toFloat()
                    "Savings and Investments" -> savingsInvestments += entry.amount.toFloat()
                    "Personal Care" -> personalCare += entry.amount.toFloat()
                    "Entertainment and Leisure" -> entertainmentLeisure += entry.amount.toFloat()
                    "Miscellaneous" -> miscellaneous += entry.amount.toFloat()
                }
            }
        }
        totalled=listOf(foodGroceries, utility, recreation, transportation, insuranceHealthcare, savingsInvestments,
            personalCare, entertainmentLeisure, miscellaneous)
        return totalled

}
//the chart(s)
@Composable
private fun AddDefaultStackedBarChart(data: Map<String, List<EntryOb>>,Month:String) {

    var barEntrys= mutableListOf<BarEntry>()
    var maximum=0f
    var totalsLabel= mutableListOf<String>()


    val types = arrayOf("Food/Groceries", "Utility", "Recreation", "Transportation", "Healthcare", "Investments", "Personal Care", "Leisure", "Miscellaneous")
    var index=0F
    var eachDay = mutableSetOf<String>()

    for (entry in data) {

        Log.d("Entry", "Date: ${entry.key}, Entries: ${entry.value}")
        var dataMonth="${entry.key.split("-")[0]}+${entry.key.split("-")[1]}"
        if(dataMonth=="${Month.split("-")[0]}+${Month.split("-")[1]}"){
            eachDay.add(entry.key)
            totalsLabel.add(entry.key)





            var totals = addTypes(entry.value,Month)
            //find maximum for putting the chart
            for(total in totals) {

                if (total > maximum) {
                    maximum = total
                }

            }



            Log.d("Totals", "Totals: ${totals}")
            barEntrys.add(BarEntry(index, totals.toFloatArray(), entry.key))
            index++
        }










    }
   var BarDataSet= BarDataSet(barEntrys,"${Month}")
    //
    BarDataSet.setColors(

            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
            Color.LTGRAY,
            Color.DKGRAY,
            Color.WHITE

        )
    BarDataSet.stackLabels=types






    var BarData= BarData(BarDataSet)
    var BarChart:BarChart= BarChart(LocalContext.current)


    BarChart.axisLeft.axisMaximum=maximum+(maximum*0.1f)
    BarChart.setFitBars(true)

    BarChart.axisRight.isEnabled = false

    BarChart.data=BarData
    BarChart.description.text="For each Day listed expenses"

    BarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM;

    BarChart.legend.isEnabled=true
    BarChart.xAxis.labelCount=barEntrys.size

    BarChart.xAxis.valueFormatter = IndexAxisValueFormatter(eachDay.toList())
    BarChart.xAxis.setDrawLabels(true)
    BarChart.xAxis.granularity = 1f
    BarChart.xAxis.labelRotationAngle = -45f



    BarChart.xAxis.position=XAxis.XAxisPosition.BOTTOM
    BarChart.legend.maxSizePercent=5f
    //legend
    BarChart.legend.verticalAlignment= Legend.LegendVerticalAlignment.TOP
    BarChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    BarChart.legend.orientation = Legend.LegendOrientation.VERTICAL

    BarChart.legend.setDrawInside(true)
    BarChart.legend.textSize = 10f









    AndroidView(factory = { BarChart}, modifier = Modifier.fillMaxWidth().height(500.dp).padding(10.dp))
    BarChart.invalidate()










}
fun organizeData(entries: List<Entry>): Map<String, List<EntryOb>> {
    return entries.groupBy {
        it.dateTime.split(" ")[0]
    }.mapValues { (_, group) ->
        group.map { EntryOb(it) }
    }
}

