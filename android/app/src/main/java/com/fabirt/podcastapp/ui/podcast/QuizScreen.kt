package com.fabirt.podcastapp.ui.podcast

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabirt.podcastapp.domain.model.OptionsQuiz
import com.fabirt.podcastapp.ui.common.PreviewContent

/**
 * Created by dion on 2023/04/30.
 */

@Composable
fun QuizScreen(optionsQuiz: OptionsQuiz) {
    QuizScreenContent(
        question = optionsQuiz.question,
        options = optionsQuiz.options,
        answer = optionsQuiz.answer
    )
}

@Composable
fun QuizScreenContent(question: String, options: List<String>, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            RadioButtonSample(options, answer)
        }
    }
}

@Composable
fun RadioButtonSample(radioOptions: List<String>, answer: String) {
    val (selectedOption: String, onOptionSelected: (String) -> Unit) = remember { mutableStateOf("") }
    val choosed = remember { mutableStateOf(false) }
    Column {
        val optionsModifier = Modifier.padding(2.dp)
        radioOptions.forEach { text ->
            val borderColor = if (selectedOption.startsWith(answer).not()) Color.Red else Color.Green
            val answerModifier = Modifier
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(2.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        enabled = choosed.value.not(),
                        selected = (text == selectedOption),
                        onClick = {
                            choosed.value = true
                            onOptionSelected(text)
                        }
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    enabled = choosed.value.not(),
                    selected = (text == selectedOption),
                    onClick = {
                        choosed.value = true
                        onOptionSelected(text)
                    }
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = if (text.startsWith(answer) && choosed.value) answerModifier else optionsModifier
                )
            }
        }
    }
}


@Preview
@Composable
fun QuizScreenDarkPreview() {
    PreviewContent(true) {
        val question = "When did YouTube Music officially roll out podcasts?"
        val options = listOf(
            "A. Last year on Android, iOS, and the web",
            "B. This month on Android, iOS, and the web",
            "C. A few months ago on Android only"
        )
        val answer = "B. This month on Android, iOS, and the web"
        QuizScreen(OptionsQuiz(question, options, answer))
    }
}