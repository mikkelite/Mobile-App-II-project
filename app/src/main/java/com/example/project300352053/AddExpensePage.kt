package com.example.project300352053

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.project300352053.data.Entry
import com.example.project300352053.data.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class AddExpenseViewModel(private val entryDao: EntryDao) : ViewModel() {
    var expenseText = MutableStateFlow("")
    var expenseGroup = MutableStateFlow("{type not selected}")
    var expenseDescription = MutableStateFlow(" ")
    var date = MutableStateFlow("")


    fun addExpense(dateArg:String): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentTime=dateFormat.format(Date())
            if(dateArg.isEmpty()){
                date.value = currentTime
            }

            val expenseAmount = expenseText.value.toIntOrNull()
            println("${expenseAmount} is the expense Amount")
            val newEntry = Entry(0,expenseText.value.toInt(), expenseDescription.value, expenseGroup.value, date.value)
            entryDao.insertAll(newEntry)
            expenseText.value = ""
            expenseDescription.value=""

        }
        return true
    }
}

    @Composable
    fun AddExpense(navController: NavHostController,viewModel: AddExpenseViewModel){
        var menuExpanded by remember { mutableStateOf(false) }
        var succesText by remember { mutableStateOf("") }

        val expenseText by viewModel.expenseText.collectAsState()
        val expenseGroup by viewModel.expenseGroup.collectAsState()
        val expenseDescription by viewModel.expenseDescription.collectAsState()
        val expenseDate by viewModel.date.collectAsState()




        //list for the type
        val mExpenses = listOf("Food/Groceries","Utility","Recreation","Transportation","Insurance and Healthcare",
            "Savings and Investments","Personal Care","Entertainment and Leisure","Miscellaneous")

        Column(modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Spacer(modifier = Modifier.height(30.dp))
            //text field for the expense
            Text("Value")
            TextField(value = expenseText,
                onValueChange = { Log.d("AddExpense", "Expense Text Changed: ${viewModel.expenseText.value}")
                    viewModel.expenseText.value=it
                   },
                label = { Text("enter expense value: ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Row {

                TextButton(onClick = { menuExpanded = true }, Modifier.padding(10.dp)) {


                    Text("Type")
                    Icon(Icons.Rounded.Edit, contentDescription = "Localized description")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                )

                {
                    mExpenses.forEach { option ->
                        DropdownMenuItem(text = { Text(option) },
                            onClick = {
                                viewModel.expenseGroup.value = option
                                menuExpanded = false
                            }
                        )

                    }
                }
            }
            //display the expense group
            Text("Expense group selected:")
            Text(expenseGroup)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Description")
            TextField(value = expenseDescription,
                onValueChange = {viewModel.expenseDescription.value=it},
                label = {
                    Text("enter a description of the purchase")
                })
            Spacer(modifier = Modifier.padding(10.dp))
            //add date manually
            Text("Add a Date Manually (PAST EXPENSE-OPTIONAL)")
            TextField(value = expenseDate,
                onValueChange = { viewModel.date.value=it },
                label = {
                    Text("enter a date in the format yyyy-MM-dd HH:mm:ss")
                }

            )
            Spacer(modifier = Modifier.padding(10.dp))


            //add the expense
            //
            //
            Button(
                onClick = {
                    if(viewModel.expenseText.value.isNotEmpty())
                        {
                            if(viewModel.addExpense(expenseDate)){
                                succesText="Expense added successfully"
                            }
                            else{
                                succesText="Expense not added something went wrong"
                            }
                        }
                    else{
                        succesText="Please enter a value for the expense and select a type"
                    }
                }


            )

            {

                Text(text = "Submit Expense")

            }


            Button(onClick = {navController.navigate("exit")}, Modifier.padding(30.dp)) {
                Text(text = "Exit to Main")

            }
            Text(succesText)


        }

    }
