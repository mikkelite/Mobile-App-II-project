package com.example.project300352053

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.project300352053.data.Entry
import com.example.project300352053.data.EntryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListEntries

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ListEntries(navController: NavHostController, entryDao: EntryDao) {
    var entries by remember { mutableStateOf(emptyList<Entry>()) }
    var isLoading by remember { mutableStateOf(true) }
    var enteredDate by remember { mutableStateOf("") }





    LaunchedEffect(entryDao) {
        val data = withContext(Dispatchers.IO) {

            entryDao.getAll()
        }
        entries=data
        isLoading = false

    }
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("Enter a Date:")
            TextField(value = enteredDate, onValueChange = { enteredDate = it })

        }
        Button(onClick = { navController.navigate("exit") },modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            (Text("exit to main"))
        }
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            var filteredEntries: List<Entry> = entries
            if (enteredDate.isNotEmpty()) {
                filteredEntries = entries.filter { it.dateTime.startsWith(enteredDate) }
            }
            LazyColumn {
                items(filteredEntries) { entry ->
                    var showDialog by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp), onClick = {

                                    showDialog=true



                                
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Amount: ${entry.amount}")
                            Text(text = "Description: ${entry.description}")
                            Text(text = "Group: ${entry.type}")
                            Text(text = "Time of entry: ${entry.dateTime}")
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Confirmation") },
                            text = { Text("Are you sure you want to proceed?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    //picks yes and deletes entries
                                    showDialog = false
                                    CoroutineScope(Dispatchers.IO).launch() {
                                        entryDao.deleteEntryById(entry.uid)

                                    }
                                    navController.navigate("listEntries")

                                }) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    // Handle "No" action here
                                    showDialog = false

                                }) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}