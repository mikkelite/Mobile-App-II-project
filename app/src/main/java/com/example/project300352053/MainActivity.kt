package com.example.project300352053


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


import com.example.project300352053.data.AppDatabase
import com.example.project300352053.data.EntryDao

import com.example.project300352053.ui.theme.Project300352053Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext

import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project300352053Theme {
                val navController =  rememberNavController()



                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java
                    , "entries"
                ).addMigrations(AppDatabase.MIGRATION_1_2).build()

                val entryDao = db.EntryDao()
                val accountDao=db.AccountDao()
                val viewModel: AddExpenseViewModel= AddExpenseViewModel(entryDao)
                val accountPage=AccountPage(accountDao,entryDao)


                NavHost(
                    navController=navController,
                    startDestination = "MainScreen"
                ){
                    composable("MainScreen"){ CreateMainPage(navController,entryDao)}
                    composable("account"){ SetTotals(accountPage,navController) }
                    composable("addExpense"){ AddExpense(navController,viewModel) }
                    composable("summary"){ Summary(navController,entryDao) }
                    composable("listEntries"){ ListEntries(navController,entryDao) }
                    composable("exit"){CreateMainPage(navController,entryDao)  }
                    composable("exitApp"){ exit() }

                }

            }
        }
    }
}




@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CreateMainPage(navController: NavHostController,entryDao: EntryDao) {
    val context= LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize()
    )
    {

        Image(
            painter = painterResource(id = R.drawable.pexels_karolina_grabowska_4475523),  // Your image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop

        )

        //COLUMN FOR EVERYTHING
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    border = BorderStroke(10.dp, Color.hsl(120F, .10F, .25F)),
                    shape = RectangleShape

                )

                .padding(10.dp)

                , horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Expense App by Michael",
                Modifier
                    .border(width = 1.dp, color = Color.hsl(120F, .30F, .25F))
                    .padding(15.dp),
                fontFamily = FontFamily.Serif,
                textDecoration = TextDecoration.Underline

            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = Color.hsl(60F, .30F, .25F))
                    .border(width = 3.dp, color = Color.hsl(120F, .30F, .25F))
                    .padding(15.dp)
                    .width(300.dp)
            ) {



                //COLUMN FOR FIRST four BUTTONS
                //add expense button


                
                Button(
                    onClick = { navController.navigate("addExpense") },
                    shape = androidx.compose.foundation.shape.CutCornerShape(0.dp),
                    //colors for the buttons
                    colors = ButtonColors(
                        contentColor = Color.hsl(110F, .20F, .85F),
                        containerColor = Color.hsl(100F, .30F, .25F),
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "Add an Expense",textDecoration = TextDecoration.Underline)

                }
                //account button
                Button(
                    onClick = { navController.navigate("account") },
                    shape = androidx.compose.foundation.shape.CutCornerShape(0.dp),
                    //colors for the buttons
                    colors = ButtonColors(
                        contentColor = Color.hsl(100F, .30F, .65F),
                        containerColor = Color.hsl(100F, .30F, .25F),
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "Account information")

                }
                //summary button
                Button(
                    onClick = { navController.navigate("summary") },
                    shape = androidx.compose.foundation.shape.CutCornerShape(0.dp),

                    //colors for the buttons
                    colors = ButtonColors(
                        contentColor = Color.hsl(100F, .30F, .65F),
                        containerColor = Color.hsl(100F, .30F, .25F),
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "Summary information by Month")

                }
                Button(
                    onClick = { navController.navigate("listEntries") },
                    shape = androidx.compose.foundation.shape.CutCornerShape(0.dp),

                    //colors for the buttons
                    colors = ButtonColors(
                        contentColor = Color.hsl(100F, .30F, .65F),
                        containerColor = Color.hsl(100F, .30F, .25F),
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "List Expenses")

                }
            }


            Spacer(modifier = Modifier.height(20.dp))
            var showDialog by remember { mutableStateOf(false)}



            //COLUMN FOR EXIT AND DELETE DATABASE BUTTONS
            Column(modifier = Modifier
                .background(color = Color.hsl(60F, .30F, .25F))
                .border(width = 3.dp, color = Color.hsl(120F, .30F, .25F))
                .padding(15.dp)
                .width(200.dp)

              , horizontalAlignment = Alignment.CenterHorizontally) {
                //exit button
                Button(
                    onClick = { navController.navigate("exitApp") },
                    modifier = Modifier.width(150.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                    //colors for the buttons
                    , colors = ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Red,
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    )
                )
                {
                    Text(text = "Exit",textDecoration = TextDecoration.Underline)

                }
                Spacer(modifier = Modifier.height(30.dp))

                //delete database button
                Button(
                    onClick =
                    {
                        showDialog = true

                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    //colors for the buttons
                    colors = ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Red,
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "Clear Database of Expenses")
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
                                    entryDao.nuketable()

                                }

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
            Spacer(modifier = Modifier.height(20.dp))

        }
    }
}


