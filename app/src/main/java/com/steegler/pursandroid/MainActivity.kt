@file:OptIn(ExperimentalFoundationApi::class)

package com.steegler.pursandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.steegler.pursandroid.ui.theme.PursAndroidTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PursAndroidTheme {
                MainView()
            }
        }
    }
}

@Composable
fun MainView(modifier: Modifier = Modifier, viewModel: MainViewModel = viewModel()) {
    var isHidden by remember { mutableStateOf(true) }
    val title by viewModel.title.collectAsState()
    val items by viewModel.items.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.requestData()
    }
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.background),
                    contentScale = ContentScale.FillBounds
                ),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .background(Color.Transparent),

                ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 54.sp,
//                                fontFamily = FontFamily(Font(R.font.fira sans)),
                        fontWeight = FontWeight(900),
                        color = Color(0xFFFFFFFF),

                        )
                )


                ScheduleView(
                    modifier = Modifier
                        .background(Color.Transparent),
                    isHidden = isHidden,
                    days = items,
                    action = {
                        viewModel.requestData()
                        isHidden = !isHidden
                    }
                )
            }
            if (isHidden)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    BottomMenuButton()
                }
        }
    }
}

@ExperimentalFoundationApi
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScheduleView(modifier: Modifier = Modifier, isHidden: Boolean = true, days: Map<DayOfWeek, List<List<LocalTime>>> = emptyMap(), viewModel: MainViewModel = viewModel(), action: () -> Unit = {}) {
    Box(

        modifier = modifier
            .border(width = 1.dp, color = Color(0xFF333333), shape = RoundedCornerShape(size = 8.dp))
            .background(color = Color(0xE5D9D9D9), shape = RoundedCornerShape(size = 8.dp))
            .clip(shape = RoundedCornerShape(8.dp))
    ) {


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(Color.Transparent)
                .padding(8.dp, end = 8.dp)
                .animateContentSize()

        ) {
            stickyHeader {
                Header(
                    modifier = Modifier.heightIn(83.dp),
                    indicatorColor = viewModel.getColor(),
                    isHidden = isHidden,
                    headerText = viewModel.getHeaderText(),
                    action = action
                )
            }

            if (!isHidden)
                items(days.toList()) {
                    ScheduleItem(element = it)
                }

        }


    }
}

@Composable
fun Header(modifier: Modifier = Modifier, indicatorColor: Color = Color.Red, headerText: String = "", isHidden: Boolean = false, action: () -> Unit) {


    Box(contentAlignment = Alignment.BottomCenter) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 8.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = headerText,
                        style = TextStyle(
                            fontSize = 15.sp,
//                    fontFamily = FontFamily(),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF333333),
                        ),
                        modifier = Modifier
                            .height(31.dp)
                            .wrapContentHeight()

                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Canvas(modifier = Modifier.size(7.dp), onDraw = {
                        drawCircle(color = indicatorColor)
                    })
                }
                Button(
                    modifier = Modifier.height(26.dp),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    border = BorderStroke(0.dp, Color.Transparent),
                    onClick = action
                )
                {
                    Text(
                        text = "SEE FULL HOURS",
                        style = TextStyle(
                            fontSize = 12.sp,
//                        fontFamily = FontFamily(Font(R.font.chivo)),
                            fontWeight = FontWeight(400),
                            color = Color(0x4F333333),

                            )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .width(24.dp)
            ) {
                Button(
                    modifier = Modifier.height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    border = BorderStroke(0.dp, Color.Transparent),
                    onClick = action
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier
                            .rotate(if (isHidden) 0f else -90f)
                    )
                }
            }
        }
        if (!isHidden)
            Row(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .padding(horizontal = 8.dp)
                    .background(color = Color.Black)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .align(Alignment.BottomCenter)
            ) {
            }

    }
}

@Composable
fun ScheduleItem(modifier: Modifier = Modifier, element: Pair<DayOfWeek, List<List<LocalTime>>>) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .heightIn(45.dp, (30 * element.second.size).dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Text(
                text = element.first.fullName,
                style = TextStyle(
                    fontSize = 18.sp,
//                    fontFamily = FontFamily(Font(R.font.hind siliguri)),
                    fontWeight = if (element.first.isToday) FontWeight.Bold else FontWeight.Normal,
                    color = Color(0xFF333333),

                    )
            )
        }
        Column {
            LazyColumn {
                items(element.second) { list ->

                    val printableFormat = DateTimeFormatter.ofPattern("hh:mm a")
                    val text =
                        if ((list.first().hour - list.last().hour).absoluteValue == 0)
                            "Open 24hrs"
                        else "${list.first().format(printableFormat)} - ${list.last().format(printableFormat)}"

                    Text(
                        modifier = Modifier.height(30.dp),
                        text = text,
                        style = TextStyle(
                            fontSize = 18.sp,
//                    fontFamily = FontFamily(Font(R.font.hind siliguri)),
                            fontWeight = if (element.first.isToday) FontWeight.Bold else FontWeight.Normal,
                            color = Color(0xFF333333),

                            )
                    )
                }
            }
        }
    }


}


@Composable
fun BottomMenuButton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = "image description",
            contentScale = ContentScale.None,
            modifier = Modifier
                .rotate(-90f)
                .width(24.dp)
                .height(24.dp),
            colorFilter = ColorFilter.tint(Color(0x80ffffff))

        )
        Image(
            painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = "image description",
            contentScale = ContentScale.None,
            modifier = Modifier
                .rotate(-90f)
                .width(24.dp)
                .height(24.dp),
            colorFilter = ColorFilter.tint(Color(0xffffffff))
        )
        Text(
            text = "View Menu",
            style = TextStyle(
                fontSize = 24.sp,
//                fontFamily = FontFamily(Font(R.font.hind siliguri)),
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    PursAndroidTheme {
        Header(modifier = Modifier.height(83.dp), headerText = "Open until 2:30 AM, reopens at 03:00 PM", action = {})
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PursAndroidTheme {
        ScheduleView()
    }
}

@Preview(showBackground = true)
@Composable
fun BottomMenuButtonPreview() {
    PursAndroidTheme {
        BottomMenuButton()
    }
}