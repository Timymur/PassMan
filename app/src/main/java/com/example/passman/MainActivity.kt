package com.example.passman

import android.R.attr.password
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.passman.ui.screens.LoginScreen
import com.example.passman.ui.screens.RegisterScreen
import com.example.passman.ui.theme.PassManTheme
import com.example.passman.util.PrefsHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassManTheme {
                AppContent()

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(){
    val context  = LocalContext.current
    var masterPassword by remember {mutableStateOf<String?>(PrefsHelper.getMasterPassword(context))}
    var isLoggedIn by remember {mutableStateOf(false)}

    when{
        masterPassword == null ->{
            RegisterScreen(onRegister = {password ->
                PrefsHelper.saveMasterPassword(context, password)
                masterPassword = password
                isLoggedIn = true
            })
        }
        !isLoggedIn ->{
            LoginScreen(onLogin = {enteredPassword->
                if(enteredPassword == masterPassword){
                    isLoggedIn = true
                } else{
                    // TO DO SOMETHING
                }
            })
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {Text("Менеджер паролей")},
                        actions = {
                            TextButton(onClick = {
                                isLoggedIn = false
                            }){
                                Text("Выйти")
                            }
                        }
                        )
                }
            ){ padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ){
                    Text("Главный экран ( список паролей) - заглушка")
                }
            }
        }

    }



}