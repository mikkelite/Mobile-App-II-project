package com.example.project300352053




import android.graphics.Typeface
import android.util.Log

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.project300352053.data.Account
import com.example.project300352053.data.AccountDao
import com.example.project300352053.data.Entry
import com.example.project300352053.data.EntryDao
import com.example.project300352053.data.EntryOb



import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class AccountPage(private val accountDoa: AccountDao,private val entryDao: EntryDao): ViewModel() {
    var Account=MutableStateFlow(Account(0,0,0,0,0,0,0,0,0))
    var totals= emptyList<Account>()

    var entrys=MutableStateFlow(emptyList<Entry>())
    var mapEntrys= MutableStateFlow(emptyMap<String,List<EntryOb>>())



    var Food =MutableStateFlow("")
    var Utility=MutableStateFlow("")
    var Recreation=MutableStateFlow("")
    var Transportation=MutableStateFlow("")
    var Healthcare=MutableStateFlow("")
    var Investments=MutableStateFlow("")
    var PersonalCare =MutableStateFlow("")
    var Miscellaneous=MutableStateFlow("")


    init {
        getAccounts()
        getEntries()
        getMapEntrys()


    }
    fun getAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            totals = accountDoa.getAllAccounts()
        }
    }
    fun addAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDoa.insert(account)

        }
    }
    //all for making the pie chart
    fun getEntries(){
        viewModelScope.launch(Dispatchers.IO) {
            entrys.value = entryDao.getAll()
        }
    }
    fun getMapEntrys(){
        mapEntrys.value=organizeData(entrys.value)
        Log.d("init", "init stuff: ${mapEntrys.value}")
    }

    fun getTotals(){
        if (!totals.isEmpty()) {
            Account.value = totals[0]
            Log.d("getTotals", "Totals: $Account.value")
        }
    }
    fun updateTotals(Account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDoa.update(Account)
        }
    }


    //each months data
    fun getTotals(data: Map<String, List<EntryOb>>,currentMonth:String):MutableMap<String,Float> {
        var totals2 = mutableMapOf<String,Float>()
        for (entry in data) {
            totals2 = addTypes(entry.value, currentMonth,totals2)
        }
    return totals2
    }
    //get totals for each slice of the pie chart
    fun addTypes(data: List<EntryOb>, Month: String, totals2: MutableMap<String, Float>):MutableMap<String,Float> {
        var totals= mutableMapOf<String,Float>()
            if(totals2.size==0)
            {
                totals["Food/Groceries"]=0f
                totals["Utility"]=0f
                totals["Recreation"]=0f
                totals["Transportation"]=0f
                totals["Insurance and Healthcare"]=0f
                totals["Savings and Investments"]=0f
                totals["Personal Care"]=0f
                totals["Entertainment and Leisure"]=0f
                totals["Miscellaneous"]=0f
                for (entry in data) {
                    var entryMonth: String =
                        "${entry.dateTime.split("-")[0]}-${entry.dateTime.split("-")[1]}"
                    if (entryMonth == Month) {
                        totals[entry.type] = (totals[entry.type] ?: 0f) + entry.amount.toFloat()

                    }
                }
                return totals

            }

            for (entry in data) {
                var entryMonth: String =
                    "${entry.dateTime.split("-")[0]}-${entry.dateTime.split("-")[1]}"
                if (entryMonth == Month) {
                    totals2[entry.type] = (totals2[entry.type] ?: 0f) + entry.amount.toFloat()

                }
            }
            return totals2


    }

}

@Composable
fun SetTotals(viewModel2: AccountPage,navController: NavHostController) {
    LaunchedEffect(viewModel2) {

        viewModel2.getMapEntrys()
        viewModel2.getTotals()


    }


    val data by viewModel2.mapEntrys.collectAsState()

    val Account by viewModel2.Account.collectAsState()



    var totals by remember { mutableStateOf(mutableMapOf<String,Float>()) }

    //update values from ui
    val food: String by viewModel2.Food.collectAsState()
    val utility by viewModel2.Utility.collectAsState()
    val recreation by viewModel2.Recreation.collectAsState()
    val transportation by viewModel2.Transportation.collectAsState()
    val healthcare by viewModel2.Healthcare.collectAsState()
    val investments by viewModel2.Investments.collectAsState()
    val personalCare by viewModel2.PersonalCare.collectAsState()
    val miscellaneous by viewModel2.Miscellaneous.collectAsState()


    //currently stored variables
    var foodOld by remember{mutableStateOf(0) }
    var utilityOld by remember{mutableStateOf(0) }
    var recreationOld by remember{mutableStateOf(0) }
    var transportationOld by remember{mutableStateOf(0) }
    var healthcareOld by remember{mutableStateOf(0) }
    var investmentsOld by remember{mutableStateOf(0) }
    var personalCareOld by remember{mutableStateOf(0) }
    var miscellaneousOld by remember{mutableStateOf(0) }
    foodOld=Account.Food
    utilityOld=Account.Utility
    recreationOld=Account.Recreation
    transportationOld=Account.Transportation
    healthcareOld=Account.Healthcare
    investmentsOld=Account.Investments
    personalCareOld=Account.PersonalCare
    miscellaneousOld=Account.MIscellaneous
    //view model data collection



    var currentMonth= ""
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentTime=dateFormat.format(Date())
    currentMonth="${currentTime.split("-")[0]}-${currentTime.split("-")[1]}"

    totals = viewModel2.getTotals(data, currentMonth)

    var colors= mutableListOf(Color.Red,Color.Blue,Color.Green,Color.Yellow,Color.Magenta,Color.Cyan,Color.LightGray)
    var slices= mutableListOf<PieChartData.Slice>()
    for (entry in totals) {
        Log.d("SetTotals", "Totals: $entry")
        if(entry.value!=0f) {
            val color=Random.nextInt(0,colors.size)
            slices.add(PieChartData.Slice(entry.key, entry.value, colors[color]))
            colors.removeAt(color)

        }

    }
    //totals to see if over budget
    var foodTotal by remember { mutableFloatStateOf(0f) }
    var utilityTotal by remember { mutableFloatStateOf(0f) }
    var recreationTotal by remember { mutableFloatStateOf(0f) }
    var transportationTotal by remember { mutableFloatStateOf(0f) }
    var healthcareTotal by remember { mutableFloatStateOf(0f) }
    var investmentsTotal by remember { mutableFloatStateOf(0f) }
    var personalCareTotal by remember { mutableFloatStateOf(0f) }
    var entertainmentTotal by remember { mutableFloatStateOf(0f) }
    var miscellaneousTotal by remember { mutableFloatStateOf(0f) }


    for (entry in totals) {
        if(entry.key=="Food/Groceries") {
            foodTotal=entry.value
        }
        else if(entry.key=="Utility") {
            utilityTotal=entry.value
        }
        else if(entry.key=="Recreation") {
            recreationTotal=entry.value
        }
        else if(entry.key=="Transportation") {
            transportationTotal=entry.value

        }
        else if(entry.key=="Insurance and Healthcare") {
            healthcareTotal=entry.value
        }
        else if(entry.key=="Savings and Investments") {
            investmentsTotal=entry.value
        }
        else if(entry.key=="Personal Care") {
            personalCareTotal=entry.value

        }
        else if(entry.key=="Entertainment and Leisure") {
            entertainmentTotal=entry.value
        }
        else if(entry.key=="Miscellaneous") {
            miscellaneousTotal=entry.value
        }

    }


    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)
    val pieChartConfig = PieChartConfig(
        sliceLabelTextColor = Color.Black,
        sliceLabelTextSize = 15.sp,
        sliceLabelTypeface = Typeface.DEFAULT_BOLD,



        labelVisible = true,
        isAnimationEnable = true,
        showSliceLabels = true,
        animationDuration = 500
    )
    

    //set the values from the database


        val scrollState = rememberScrollState()




        Column(

            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text("Set the Maximum for each Group for the month")
            Text("Food")
            TextField(value = food, onValueChange = { viewModel2.Food.value = it }
            )
            Text("Old/Current Food Value: $foodOld")
            if(foodTotal>foodOld){
                Text("you are over spending on food:$foodTotal",color=Color.Red)
            }
            Text("Utility")
            TextField(
                value = utility,
                onValueChange = { viewModel2.Utility.value = it })
            Text("Old/Current Utility Value: $utilityOld")
            if(utilityTotal>utilityOld){
                Text("you are over spending on utility:$utilityTotal",color=Color.Red)
            }
            Text("Recreation")
            TextField(
                value = recreation,
                onValueChange = { viewModel2.Recreation.value = it })
            Text("Old/Current Recreation Value: $recreationOld")
            if(recreationTotal>recreationOld){
                Text("you are over spending on recreation: $recreationTotal",color=Color.Red)
            }
            Text("Transportation")
            TextField(
                value = transportation,
                onValueChange = { viewModel2.Transportation.value = it })
            Text("Old/Current Transportation Value: $transportationOld")
            if(transportationTotal>transportationOld){
                Text("you are over spending on transportation:$transportationTotal",color=Color.Red)
            }
            Text("Healthcare")
            TextField(
                value = healthcare,
                onValueChange = { viewModel2.Healthcare.value = it })
            Text("Old/Current Healthcare Value: $healthcareOld")
            if(healthcareTotal>healthcareOld){
                Text("you are over spending on healthcare: $healthcareTotal",color=Color.Red)
            }
            Text("Investments")
            TextField(
                value = investments,
                onValueChange = { viewModel2.Investments.value = it })
            Text("Old/Current Investments Value: $investmentsOld")
            if(investmentsTotal>investmentsOld){
                Text("you are over spending on investments : $investmentsTotal",color=Color.Red)
            }
            Text("Personal Care")
            TextField(
                value = personalCare,
                onValueChange = { viewModel2.PersonalCare.value = it })
            Text("Old Personal Care Value: $personalCareOld")
            if(personalCareTotal>personalCareOld){
                Text("you are over spending on personal care : $personalCareTotal",color=Color.Red)
            }
            Text("Miscellaneous")
            TextField(
                value = miscellaneous,
                onValueChange = { viewModel2.Miscellaneous.value = it })
            Text("Old/Current Miscellaneous Value: $miscellaneousOld")
            if(miscellaneousTotal>miscellaneousOld){
                Text("you are over spending on miscellaneous items: $miscellaneousTotal",color=Color.Red)
            }
            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                val account = Account(
                    0, food.toInt(), utility.toInt(), recreation.toInt(), transportation.toInt(), healthcare.toInt(), investments.toInt(), personalCare.toInt(), miscellaneous.toInt()
                )


                viewModel2.updateTotals(account)
                viewModel2.getAccounts()

                viewModel2.getTotals()
                navController.navigate("account")




            }) {
                Text("Update Totals")

            }
            Button(onClick = { navController.navigate("exit") })
            { Text("Back to Main Page") }


            Text("if you want to add an profile and you haven't added one before", textAlign = TextAlign.Center)
            Button(onClick = {
                viewModel2.addAccount(Account(0,food.toInt(),utility.toInt(),
                    recreation.toInt(),transportation.toInt(),healthcare.toInt(),
                    investments.toInt(),personalCare.toInt(),miscellaneous.toInt()))
                navController.navigate("account")
            })




            {
                Text("Add Account")
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text("Pie Chart of monthly totals")

            if(slices.isNotEmpty()) {
                PieChart(
                    modifier = Modifier.fillMaxSize(),
                    pieChartData,
                    pieChartConfig
                )

            }



        }
}


