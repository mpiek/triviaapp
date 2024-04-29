package com.example.triviaapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triviaapp.ui.theme.TriviaAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.GlobalScope as GlobalScope1


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("triviaapp.token", Context.MODE_PRIVATE)
        setContent {
            TriviaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        var question by remember { mutableStateOf("Click a button to fetch a question") }
                        var answer by remember { mutableStateOf("") }
                        var showAnswer by remember { mutableStateOf(false) }
                        var funFact by remember { mutableStateOf("") }
                        var multiple_choice by remember { mutableStateOf("") }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clickable { showAnswer = !showAnswer },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = question,
                                    fontSize = 28.sp,
                                    textAlign = TextAlign.Center
                                )
                                if (multiple_choice.isNotEmpty()) {
                                    Text(
                                        text = multiple_choice,
                                        fontSize = 24.sp,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                if (showAnswer) {
                                    Divider(color = Color.Black, thickness = 2.dp)
                                    Text(
                                        text = "Answer: $answer",
                                        fontSize = 24.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = funFact,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(
                                    onClick = {
                                        showAnswer = false; question = ""; multiple_choice = "" ; answer =
                                        ""; fetchQuestion("Entertainment", sharedPreferences) { m, q, a ->
                                        question = q; answer = a ; multiple_choice = m
                                    }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE91E63)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Entertainment")
                                }
                                Button(
                                    onClick = {
                                        showAnswer = false; question = ""; multiple_choice = "" ; answer =
                                        ""; fetchQuestion("General Knowledge", sharedPreferences) { m, q, a ->
                                        question = q; answer = a ; multiple_choice = m
                                    }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDAA520)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("General Knowledge")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(
                                    onClick = {
                                        showAnswer = false; question = ""; multiple_choice = "" ; answer =
                                        ""; fetchQuestion("Geography", sharedPreferences) { m, q, a ->
                                        question = q; answer = a ; multiple_choice = m
                                    }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD2B48C)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Geography")
                                }
                                Button(
                                    onClick = {
                                        showAnswer = false; question = ""; multiple_choice = "" ; answer =
                                        ""; fetchQuestion("Animals", sharedPreferences) { m, q, a ->
                                        question = q; answer = a ; multiple_choice = m
                                    }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF40E0D0)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Animals")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(
                                    onClick = {
                                        showAnswer = false; question = ""; multiple_choice = "" ; answer =
                                        ""; fetchQuestion("Other", sharedPreferences) { m, q, a ->
                                        question = q; answer = a ; multiple_choice = m
                                    }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF87CEFA)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Other")
                                }
                                Button(
                                    onClick = {
                                        showAnswer = false; question = ""; multiple_choice = "" ; answer =
                                        ""; fetchQuestion("History", sharedPreferences) { m, q, a ->
                                        question = q; answer = a ; multiple_choice = m
                                    }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF0000)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("History")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun fetchQuestion(category: String, sharedPreferences: SharedPreferences, onResult: (String, String, String) -> Unit) {
    GlobalScope1.launch(Dispatchers.IO) {
        val token = fetchAndPersistToken(sharedPreferences)
        val apiUrl = when (category) {
            "General Knowledge" -> "https://opentdb.com/api.php?amount=1&category=9"
            "Entertainment" -> {
                val entertainmentCategories =
                    listOf("10", "11", "12", "13", "14", "15", "16", "29", "32")
                val randomCategory = entertainmentCategories.random()
                "https://opentdb.com/api.php?amount=1&category=$randomCategory"
            }

            "Geography" -> "https://opentdb.com/api.php?amount=1&category=22"
            "History" -> "https://opentdb.com/api.php?amount=1&category=23"
            "Animals" -> "https://opentdb.com/api.php?amount=1&category=27"
            "Other" -> {
                val otherCategories = listOf("17", "18", "19", "20", "24", "25", "26", "30")
                val randomCategory = otherCategories.random()
                "https://opentdb.com/api.php?amount=1&category=$randomCategory"
            }

            else -> "https://opentdb.com/api.php?amount=1"
        } + "&token=$token"

        try {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            var multiple_choice = ""
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            val result = json.getJSONArray("results").getJSONObject(0)
            val type = result.getString("type")

            var question =
                Html.fromHtml(result.getString("question"), Html.FROM_HTML_MODE_LEGACY).toString()
            val correctAnswer =
                Html.fromHtml(result.getString("correct_answer"), Html.FROM_HTML_MODE_LEGACY)
                    .toString()
            if (type == "boolean") {
                question = "True or False? \n $question"
            } else if (type == "multiple") {
                val incorrectAnswers = result.getJSONArray("incorrect_answers")
                val allAnswers = mutableListOf<String>()
                allAnswers.add(correctAnswer)
                for (i in 0 until incorrectAnswers.length()) {
                    allAnswers.add(
                        Html.fromHtml(incorrectAnswers.getString(i), Html.FROM_HTML_MODE_LEGACY)
                            .toString()
                    )
                }
                allAnswers.shuffle()
               multiple_choice +=  allAnswers.joinToString("\n") { "• $it" }
            }

            onResult(multiple_choice, question, correctAnswer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private suspend fun fetchAndPersistToken(sharedPreferences: SharedPreferences): String = withContext(Dispatchers.IO) {
    val existingToken = sharedPreferences.getString("triviaapp.token", "")
    if (!existingToken.isNullOrEmpty()) {
        return@withContext existingToken
    }
    val url = URL("https://opentdb.com/api_token.php?command=request")
    val connection = url.openConnection() as HttpURLConnection
    connection.connect()
    val response = connection.inputStream.bufferedReader().use { it.readText() }
    val json = JSONObject(response)
    val token = json.getString("token")
    sharedPreferences.edit().putString("triviaapp.token", token).apply()
    return@withContext token
}



@Preview(showBackground = true)
@Composable
fun FetchQuestionPreview() {
    TriviaAppTheme {
        true
    }
}



