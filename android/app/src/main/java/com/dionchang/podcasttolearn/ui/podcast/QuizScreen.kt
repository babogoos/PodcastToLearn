package com.dionchang.podcasttolearn.ui.podcast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dionchang.podcasttolearn.R
import com.dionchang.podcasttolearn.domain.model.OptionsQuiz
import com.dionchang.podcasttolearn.ui.common.PreviewContent

/**
 * Created by dion on 2023/04/30.
 */

@Composable
fun QuizScreen(optionsQuizs: List<OptionsQuiz>, onPlaybackClick: (Long) -> Unit) {
    QuizScreenContent(optionsQuizs, onPlaybackClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizScreenContent(optionsQuizs: List<OptionsQuiz>, onPlaybackClick: (Long) -> Unit = {}) {
    Box {
        val pagerState = rememberPagerState()
        HorizontalPager(
            pageCount = optionsQuizs.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val optionsQuiz = optionsQuizs[it]
            Card(
                border = BorderStroke(1.dp, Color.White),
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
                        text = optionsQuiz.question,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    RadioButtonSample(optionsQuiz.options, optionsQuiz.answer)

                    Row(
                        Modifier
                            .align(Alignment.End)
                            .clickable {
                                onPlaybackClick.invoke(optionsQuiz.paragraphId)
                            }) {

                        Text(
                            text = "Brush Up",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )

                        Image(
                            painter = painterResource(R.drawable.ic_round_play_arrow),
                            contentDescription = "",
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(optionsQuizs.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(20.dp)

                    )
                }
            }
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
        QuizScreenContent(listOf(OptionsQuiz(question, options, answer, 1), OptionsQuiz(question, options, answer, 2)))
    }
}