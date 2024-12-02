package com.example.project300352053

import android.util.Log
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

import androidx.navigation.NavHostController
import com.example.project300352053.data.Entry
import com.example.project300352053.data.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddExpenseViewModel(private val entryDao: EntryDao) : ViewModel() {
    var expenseText = MutableStateFlow("")
    var expenseGroup = MutableStateFlow("{type not selected}")
    var expenseDescription = MutableStateFlow(" ")
    var date = MutableStateFlow("")
    var isErrorAmount = MutableStateFlow(false)
    var isGroupError = MutableStateFlow(false)
    var isDateError = MutableStateFlow(false)
    var success = MutableStateFlow(false)


    //check if the expense is valid
    fun works(dateArg:String):Boolean{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime=dateFormat.format(Date())
        val regex=Regex("^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]).*\$")
        var testVariable:Boolean
        testVariable=regex.matches(dateArg)
        Log.d("is Successful regex", "${testVariable}")
        //date errors or fill in values
        if (dateArg.isEmpty()) {
            date.value = currentTime
        } else if (regex.matches(dateArg)) {
            date.value = dateArg
            isDateError.value = false // Clear error if the date is valid
        } else {
            isDateError.value = true // Invalid date
        }

        //errors for the group selection
        if(expenseGroup.value=="{type not selected}"){
            isGroupError.value=true
        } else{
            isGroupError.value=false

        }

        //amount error checking
        if(expenseText.value.isEmpty()){
            isErrorAmount.value=true

        } else if(expenseText.value.toIntOrNull()==null){
            isErrorAmount.value=true
        } else{
            isErrorAmount.value=false
        }
        //no errors
        if(isErrorAmount.value != true && isGroupError.value != true && isDateError.value != true) {

            success.value=true
        } else{
            success.value=false
        }
        Log.d("is Successful after checking error variables", "${success.value}")
        return success.value

    }
    //add the expense

    fun addExpense(){
        viewModelScope.launch(Dispatchers.IO) {

            if(success.value) {




                val newEntry = Entry(
                    0,
                    expenseText.value.toInt(),
                    expenseDescription.value,
                    expenseGroup.value,
                    date.value
                )
                entryDao.insertAll(newEntry)
                expenseText.value = ""
                expenseDescription.value = ""
                expenseGroup.value="{type not selected}"




            }

        }

    }
}

    @Composable
    fun AddExpense(navController: NavHostController,viewModel: AddExpenseViewModel){
        var menuExpanded by remember { mutableStateOf(false) }
        var succesText by remember { mutableStateOf("") }
        var errorAmount = viewModel.isErrorAmount.collectAsState()
        var errorGroup = viewModel.isGroupError.collectAsState()
        var dateError = viewModel.isDateError.collectAsState()
        val expenseText by viewModel.expenseText.collectAsState()
        val expenseGroup by viewModel.expenseGroup.collectAsState()
        val expenseDescription by viewModel.expenseDescription.collectAsState()
        val expenseDate by viewModel.date.collectAsState()
        var success = remember { mutableStateOf(false) }





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
            if(errorAmount.value){
                Text("Please enter a value for the expense\n or enter a number without decimals")
            }

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
            if(errorGroup.value){
                Text("Please select a type for the expense")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Description")
            TextField(value = expenseDescription,
                onValueChange = {viewModel.expenseDescription.value=it},
                label = {
                    Text("enter a description of the purchase")
                })
            Spacer(modifier = Modifier.padding(10.dp))
            //add date manually
            Text("(PAST EXPENSE-OPTIONAL) Add a Date Manually ")
            TextField(value = expenseDate,
                onValueChange = { viewModel.date.value=it },
                label = {
                    Text("enter a date in the format yyyy-MM-dd HH:mm:ss")
                }

            )
            if(dateError.value){
                Text("Please enter a date in the format\n " +
                        "yyyy-MM-dd HH:mm:ss with " +
                        "\nyyyy-mm-dd being mandatory")
            }
            Spacer(modifier = Modifier.padding(10.dp))


            //add the expense
            //
            //
            Button(
                onClick = {
                    success.value=viewModel.works(expenseDate)

                    if(viewModel.expenseText.value.isNotEmpty()) {
                            if(success.value){
                                viewModel.addExpense()
                                succesText="Expense added successfully"
                            }
                            else{
                                succesText="Expense not added something went wrong"
                            }
                    } else{
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
