package com.example.project300352053

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListEntries {
}
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
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            var filteredEntries: List<Entry> = entries
            if (enteredDate.isNotEmpty()) {
                filteredEntries = entries.filter { it.dateTime.startsWith(enteredDate) }
            }
            LazyColumn {
                items(filteredEntries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
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
                }
            }
        }
    }
}