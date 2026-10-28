package com.example.passman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passman.data.model.PasswordEntry
import com.example.passman.data.repository.PasswordRepository
import com.example.passman.ui.screens.EntryDetailScreen
import com.example.passman.ui.screens.LoginScreen
import com.example.passman.ui.screens.RegisterScreen
import com.example.passman.ui.theme.PassManTheme
import com.example.passman.ui.viewmodel.PasswordViewModel
import com.example.passman.util.PrefsHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = (application as App).database
        val repository = PasswordRepository(database.passwordDao())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PasswordViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PasswordViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            PassManTheme {
                AppContent(factory = factory)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(factory: ViewModelProvider.Factory) {
    val context = LocalContext.current
    var masterPassword by remember { mutableStateOf(PrefsHelper.getMasterPassword(context)) }
    var isLoggedIn by remember { mutableStateOf(false) }

    val viewModel: PasswordViewModel = viewModel(factory = factory)
    val entries by viewModel.allEntries.observeAsState(emptyList())

    var showAddScreen by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<PasswordEntry?>(null) }
    var editingEntry by remember { mutableStateOf<PasswordEntry?>(null) } // <-- новое состояние

    when {
        masterPassword == null -> {
            RegisterScreen(onRegister = { password ->
                PrefsHelper.saveMasterPassword(context, password)
                masterPassword = password
                isLoggedIn = true
            })
        }
        !isLoggedIn -> {
            LoginScreen(onLogin = { enteredPassword ->
                if (enteredPassword == masterPassword) {
                    isLoggedIn = true
                }
            })
        }
        selectedEntry != null -> {
            EntryDetailScreen(
                entry = selectedEntry!!,
                onBack = { selectedEntry = null },
                onDelete = {
                    viewModel.deleteEntry(selectedEntry!!)
                    selectedEntry = null
                },
                onEdit = {
                    editingEntry = selectedEntry // <-- передаём в редактирование
                    selectedEntry = null // закрываем экран деталей
                }
            )
        }
        showAddScreen || editingEntry != null -> {
            AddEditEntryScreen(
                existingEntry = editingEntry, // <-- передаём запись для заполнения
                onSave = { title, username, password, notes ->
                    if (editingEntry == null) {
                        // Создаём новую запись
                        viewModel.insertEntry(
                            PasswordEntry(
                                title = title,
                                username = username,
                                password = password,
                                notes = notes
                            )
                        )
                    } else {
                        // Обновляем существующую
                        viewModel.updateEntry(
                            editingEntry!!.copy(
                                title = title,
                                username = username,
                                password = password,
                                notes = notes
                            )
                        )
                    }
                    showAddScreen = false
                    editingEntry = null
                },
                onCancel = {
                    showAddScreen = false
                    editingEntry = null
                }
            )
        }
        else -> {
            MainScreen(
                entries = entries,
                onAddClick = { showAddScreen = true },
                onEntryClick = { entry -> selectedEntry = entry }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    entries: List<PasswordEntry>,
    onAddClick: () -> Unit,
    onEntryClick: (PasswordEntry) -> Unit // <- новый параметр
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Менеджер паролей") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Text("+") }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Список пуст. Нажмите +, чтобы добавить.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(entries, key = { it.id }) { entry ->
                    ListItem(
                        headlineContent = { Text(entry.title) },
                        supportingContent = { Text(entry.username) },
                        modifier = Modifier.clickable { onEntryClick(entry) } // <- клик
                    )
                    Divider()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryScreen(
    existingEntry: PasswordEntry? = null, // <-- новое
    onSave: (title: String, username: String, password: String, notes: String) -> Unit,
    onCancel: () -> Unit
) {
    // Если передана запись, заполняем поля её данными
    var title by remember { mutableStateOf(existingEntry?.title ?: "") }
    var username by remember { mutableStateOf(existingEntry?.username ?: "") }
    var password by remember { mutableStateOf(existingEntry?.password ?: "") }
    var notes by remember { mutableStateOf(existingEntry?.notes ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (existingEntry == null) "Новая запись" else "Редактирование") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Заметки") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Отмена")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank() && password.isNotBlank()) {
                            onSave(title, username, password, notes)
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}