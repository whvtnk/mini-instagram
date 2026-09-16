package com.example.inst

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InstagramApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            InstagramBottomNavigation(
                selectedItem = currentScreen,
                onItemSelected = { currentScreen = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                0 -> InstagramHomeScreen()
                4 -> InstagramProfileScreen()
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Other Screen")
                }
            }
        }
    }
}

@Composable
fun InstagramHomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        HomeTopBar()
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    StoriesSection()
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
                items(3) {
                    PostItem()
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconPlaceholder(
            modifier = Modifier.size(28.dp),
            contentDescription = stringResource(id = R.string.content_desc_camera)
        )
        Text(
            text = stringResource(id = R.string.instagram),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
        Box {
            IconPlaceholder(
                modifier = Modifier.size(28.dp),
                contentDescription = stringResource(id = R.string.content_desc_direct)
            )
            NotificationBadge(
                count = "4",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-4).dp)
            )
        }
    }
}

@Composable
fun StoriesSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Stories", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.semantics(mergeDescendants = true) { }) {
                IconPlaceholder(modifier = Modifier.size(14.dp), contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(id = R.string.watch_all), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        LazyRow(
            modifier = Modifier.padding(top = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listOf("View Story", "Love_Story", "Design_Story", "Bad_Story", "Fun_Story")) { name ->
                StoryCircle(name = name)
            }
        }
    }
}

@Composable
fun StoryCircle(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFD600), Color(0xFFFF0000), Color(0xFFC90083)),
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = name, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun PostItem(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.your_username), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconPlaceholder(
                        modifier = Modifier.size(14.dp),
                        contentDescription = null,
                        color = Color(0xFF3897F0),
                        isCircle = true
                    )
                }
                Text(text = stringResource(id = R.string.place_city), fontSize = 11.sp, color = Color.Gray)
            }
            IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_more))
        }

        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFFF5F5F5))) {
            Text(
                text = "1/4",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            
            LikeFloatingBadge(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconPlaceholder(modifier = Modifier.size(28.dp), contentDescription = stringResource(id = R.string.content_desc_heart))
            Spacer(modifier = Modifier.width(16.dp))
            IconPlaceholder(modifier = Modifier.size(28.dp), contentDescription = stringResource(id = R.string.content_desc_comment))
            Spacer(modifier = Modifier.width(16.dp))
            IconPlaceholder(modifier = Modifier.size(28.dp), contentDescription = stringResource(id = R.string.content_desc_share))
            
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                repeat(4) { i ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (i == 0) Color(0xFF3897F0) else Color(0xFFCCCCCC))
                    )
                    if (i < 3) Spacer(modifier = Modifier.width(4.dp))
                }
            }

            IconPlaceholder(modifier = Modifier.size(28.dp), contentDescription = stringResource(id = R.string.content_desc_bookmark))
        }

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.LightGray))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append("likes ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("her_username ")
                        }
                        append("and ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("1945 more")
                        }
                    },
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("your_username ")
                    }
                    append("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed diam nonummy... ")
                    withStyle(style = SpanStyle(color = Color.Gray)) {
                        append("more")
                    }
                },
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(id = R.string.view_comments), color = Color.Gray, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InstagramProfileScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        ProfileTopBar()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ProfileHeaderSection()
                ProfileBioSection()
                EditProfileButton()
                HighlightSection()
                ProfileTabs()
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(400.dp),
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(9) {
                        Box(modifier = Modifier.aspectRatio(1f).background(Color(0xFFEEEEEE)))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_add))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.username), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(4.dp))
            IconPlaceholder(modifier = Modifier.size(16.dp), color = Color(0xFF3897F0), contentDescription = null, isCircle = true)
            IconPlaceholder(modifier = Modifier.size(16.dp), contentDescription = null)
        }
        Box {
            IconPlaceholder(modifier = Modifier.size(28.dp), contentDescription = stringResource(id = R.string.content_desc_menu))
            NotificationBadge(
                count = "1",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
            )
        }
    }
}

@Composable
fun ProfileHeaderSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.size(80.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .padding(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFE0E0E0)))
            }
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF3897F0), CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        ProfileStat(number = "150", label = stringResource(id = R.string.posts))
        ProfileStat(number = "28k", label = stringResource(id = R.string.followers))
        ProfileStat(number = "37", label = stringResource(id = R.string.following))
    }
}

@Composable
fun ProfileStat(number: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, fontSize = 13.sp)
    }
}

@Composable
fun ProfileBioSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(text = stringResource(id = R.string.your_name), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = stringResource(id = R.string.description), color = Color.Gray, fontSize = 14.sp)
        Text(text = stringResource(id = R.string.text_goes_here), fontSize = 14.sp)
        Text(text = stringResource(id = R.string.website), color = Color(0xFF00376B), fontSize = 14.sp)
    }
}

@Composable
fun EditProfileButton(modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Text(text = stringResource(id = R.string.edit_profile), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HighlightSection(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.padding(bottom = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HighlightItem(name = stringResource(id = R.string.new_story), isNew = true) }
        item { HighlightItem(name = stringResource(id = R.string.love)) }
        item { HighlightItem(name = stringResource(id = R.string.mine)) }
        item { HighlightItem(name = stringResource(id = R.string.us)) }
    }
}

@Composable
fun HighlightItem(name: String, modifier: Modifier = Modifier, isNew: Boolean = false) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(1.dp, Color.LightGray, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isNew) {
                Text("+", fontSize = 32.sp, color = Color.Black)
            } else {
                Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.Gray))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = name, fontSize = 12.sp)
    }
}

@Composable
fun ProfileTabs(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TabIcon(selected = true, contentDescription = stringResource(id = R.string.content_desc_grid))
        TabIcon(selected = false, contentDescription = stringResource(id = R.string.content_desc_igtv))
        TabIcon(selected = false, contentDescription = stringResource(id = R.string.content_desc_tagged))
    }
}

@Composable
fun TabIcon(selected: Boolean, modifier: Modifier = Modifier, contentDescription: String?) {
    Column(
        modifier = modifier.height(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconPlaceholder(
            modifier = Modifier.size(24.dp),
            color = if (selected) Color.Black else Color.Gray,
            contentDescription = contentDescription
        )
        if (selected) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
        }
    }
}

@Composable
fun InstagramBottomNavigation(selectedItem: Int, onItemSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = { 
                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 32.dp)
                        .then(if (selectedItem == 0) Modifier.background(Color(0xFFF3E5F5), RoundedCornerShape(16.dp)) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_home))
                }
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = { IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_search)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = { IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_add)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = { IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_heart)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = { onItemSelected(4) },
            icon = { IconPlaceholder(modifier = Modifier.size(24.dp), contentDescription = stringResource(id = R.string.content_desc_profile)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}

@Composable
fun IconPlaceholder(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    contentDescription: String? = null,
    isCircle: Boolean = false
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.05f), if (isCircle) CircleShape else RectangleShape)
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, color.copy(alpha = 0.4f), if (isCircle) CircleShape else RectangleShape)
        )
    }
}

@Composable
fun NotificationBadge(count: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(18.dp)
            .background(Color(0xFFFE2B54), CircleShape)
            .border(1.5.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = count, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LikeFloatingBadge(modifier: Modifier = Modifier) {
    val heartShape = remember {
        GenericShape { size, _ ->
            val width = size.width
            val height = size.height
            moveTo(width / 2f, height * 0.9f)
            cubicTo(0f, height * 0.6f, 0f, height * 0.1f, width * 0.5f, height * 0.1f)
            cubicTo(width, height * 0.1f, width, height * 0.6f, width * 0.5f, height * 0.9f)
            close()
        }
    }

    Box(
        modifier = modifier
            .width(48.dp)
            .height(44.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(Color(0xFFFF2D55), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.White, heartShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "8", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
        // Triangle tail
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-4).dp)
                .background(Color(0xFFFF2D55), GenericShape { size, _ ->
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InstagramHomePreview() {
    MaterialTheme {
        InstagramHomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun InstagramProfilePreview() {
    MaterialTheme {
        InstagramProfileScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun InstagramAppPreview() {
    MaterialTheme {
        InstagramApp()
    }
}
