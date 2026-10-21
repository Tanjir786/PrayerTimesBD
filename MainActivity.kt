package com.tanjir.prayertimesbd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.*

private val Teal = Color(0xFF004D52)
private val DeepTeal = Color(0xFF00464B)
private val Orange = Color(0xFFFF922E)
private val Panel = Color(0xFF1D2A2D)
private val Muted = Color(0xFFAAB7BA)
private val White = Color(0xFFF7FAFA)

data class District(val name: String, val lat: Double, val lon: Double)
data class Prayer(val name: String, val start: LocalTime, val end: LocalTime)

private val districts = listOf(
    District("ঢাকা",23.8103,90.4125), District("গাজীপুর",24.0023,90.4264), District("নারায়ণগঞ্জ",23.6238,90.5000),
    District("নরসিংদী",23.9229,90.7177), District("মুন্সীগঞ্জ",23.5422,90.5305), District("মানিকগঞ্জ",23.8644,90.0047),
    District("টাঙ্গাইল",24.2513,89.9167), District("কিশোরগঞ্জ",24.4260,90.9821), District("ফরিদপুর",23.6071,89.8429),
    District("রাজবাড়ী",23.7574,89.6445), District("গোপালগঞ্জ",23.0051,89.8266), District("মাদারীপুর",23.1641,90.1897),
    District("শরীয়তপুর",23.2423,90.4348), District("চট্টগ্রাম",22.3569,91.7832), District("কক্সবাজার",21.4272,92.0058),
    District("কুমিল্লা",23.4607,91.1809), District("ব্রাহ্মণবাড়িয়া",23.9608,91.1115), District("চাঁদপুর",23.2333,90.6667),
    District("ফেনী",23.0159,91.3976), District("লক্ষ্মীপুর",22.9447,90.8282), District("নোয়াখালী",22.8696,91.0995),
    District("রাঙ্গামাটি",22.7324,92.2985), District("খাগড়াছড়ি",23.1193,91.9847), District("বান্দরবান",22.1953,92.2184),
    District("সিলেট",24.8949,91.8687), District("মৌলভীবাজার",24.4829,91.7774), District("হবিগঞ্জ",24.3745,91.4155), District("সুনামগঞ্জ",25.0658,91.3950),
    District("রাজশাহী",24.3745,88.6042), District("নাটোর",24.4206,89.0000), District("নওগাঁ",24.7936,88.9318),
    District("চাঁপাইনবাবগঞ্জ",24.5965,88.2775), District("পাবনা",24.0064,89.2372), District("সিরাজগঞ্জ",24.4534,89.7007),
    District("বগুড়া",24.8465,89.3770), District("জয়পুরহাট",25.0968,89.0227), District("রংপুর",25.7439,89.2752),
    District("দিনাজপুর",25.6279,88.6332), District("ঠাকুরগাঁও",26.0337,88.4617), District("পঞ্চগড়",26.3411,88.5542),
    District("নীলফামারী",25.9310,88.8560), District("লালমনিরহাট",25.9923,89.2847), District("কুড়িগ্রাম",25.8054,89.6362),
    District("গাইবান্ধা",25.3288,89.5281), District("ঢাকুরিয়া",25.8000,89.0000), District("খুলনা",22.8456,89.5403),
    District("বাগেরহাট",22.6516,89.7859), District("সাতক্ষীরা",22.7185,89.0705), District("যশোর",23.1664,89.2081),
    District("নড়াইল",23.1725,89.5127), District("মাগুরা",23.4873,89.4199), District("ঝিনাইদহ",23.5448,89.1539),
    District("কুষ্টিয়া",23.9013,89.1205), District("চুয়াডাঙ্গা",23.6402,88.8418), District("মেহেরপুর",23.7622,88.6318),
    District("বরিশাল",22.7010,90.3535), District("ভোলা",22.6859,90.6482), District("পটুয়াখালী",22.3596,90.3299),
    District("বরগুনা",22.1590,90.1250), District("ঝালকাঠি",22.6406,90.1987), District("পিরোজপুর",22.5841,89.9720),
    District("ময়মনসিংহ",24.7471,90.4203), District("জামালপুর",24.9375,89.9378), District("শেরপুর",25.0205,90.0153), District("নেত্রকোনা",24.8700,90.7278)
)

private fun normalize(x: Double): Double = ((x % 360.0) + 360.0) % 360.0
private fun sunPosition(day: Int): Pair<Double, Double> {
    val g = 2 * Math.PI / 365.0 * (day - 1)
    val decl = 0.006918 - 0.399912*cos(g) + 0.070257*sin(g) - 0.006758*cos(2*g) + 0.000907*sin(2*g) - 0.002697*cos(3*g) + 0.00148*sin(3*g)
    val eq = 229.18 * (0.000075 + 0.001868*cos(g) - 0.032077*sin(g) - 0.014615*cos(2*g) - 0.040849*sin(2*g))
    return decl to eq
}
private fun hourAngle(lat: Double, decl: Double, altitude: Double): Double {
    val c = (sin(Math.toRadians(altitude)) - sin(Math.toRadians(lat))*sin(decl)) / (cos(Math.toRadians(lat))*cos(decl))
    return Math.toDegrees(acos(c.coerceIn(-1.0, 1.0)))
}
private fun timeFromMinutes(m: Double): LocalTime {
    val rounded = (m.roundToInt() % 1440 + 1440) % 1440
    return LocalTime.of(rounded / 60, rounded % 60)
}
private fun calculate(d: District, date: LocalDate): List<Prayer> {
    val (decl, eq) = sunPosition(date.dayOfYear)
    val tz = 6.0
    val noon = 720.0 - 4.0*d.lon + 60.0*tz - eq
    val sunrise = noon - 4.0*hourAngle(d.lat, decl, -0.833)
    val sunset = noon + 4.0*hourAngle(d.lat, decl, -0.833)
    val fajr = noon - 4.0*hourAngle(d.lat, decl, -18.0)
    val isha = noon + 4.0*hourAngle(d.lat, decl, -17.0)
    val asrAngle = Math.toDegrees(atan(1.0 / (1.0 + tan(abs(Math.toRadians(d.lat) - decl)))))
    val asr = noon + 4.0*hourAngle(d.lat, decl, 90.0 - asrAngle)
    val dhuhr = noon + 3.0
    return listOf(
        Prayer("ফজর", timeFromMinutes(fajr), timeFromMinutes(sunrise)),
        Prayer("যুহর", timeFromMinutes(dhuhr), timeFromMinutes(asr)),
        Prayer("আসর", timeFromMinutes(asr), timeFromMinutes(sunset)),
        Prayer("মাগরিব", timeFromMinutes(sunset), timeFromMinutes(isha)),
        Prayer("ইশা", timeFromMinutes(isha), timeFromMinutes(fajr + 1440.0))
    )
}
private fun bnDigits(s: String): String = s.map { if (it in '0'..'9') ('০'.code + it.code - '0'.code).toChar() else it }.joinToString("")
private fun fmt(t: LocalTime): String = bnDigits(t.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US)))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PrayerApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerApp() {
    var selected by remember { mutableStateOf(districts.first { it.name == "মানিকগঞ্জ" }) }
    var picker by remember { mutableStateOf(false) }
    var alarms by remember { mutableStateOf(setOf<String>()) }
    var tab by remember { mutableIntStateOf(0) }
    var date by remember { mutableStateOf(LocalDate.now(ZoneId.of("Asia/Dhaka"))) }
    val prayers = remember(selected, date) { calculate(selected, date) }
    val now = LocalTime.now(ZoneId.of("Asia/Dhaka"))
    val next = prayers.firstOrNull { now < it.start } ?: prayers.first()
    val nextStart = if (now < next.start) next.start else next.start.plusHours(24)
    val minsLeft = ((nextStart.toSecondOfDay() - now.toSecondOfDay()).coerceAtLeast(0) / 60)
    val remaining = "%02d:%02d".format(minsLeft / 60, minsLeft % 60)
    val progress = (1f - minsLeft / (24f * 60f)).coerceIn(0.05f, 1f)

    MaterialTheme(colorScheme = darkColorScheme(primary = Orange, background = Teal, surface = Panel, onPrimary = White, onBackground = White, onSurface = White)) {
        Column(Modifier.fillMaxSize().background(Teal)) {
            Column(Modifier.weight(0.49f).fillMaxWidth().padding(horizontal = 18.dp)) {
                Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, tint = White, modifier = Modifier.size(34.dp))
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("বৃহঃ, ১০ জমাদাল উলা", fontSize = 22.sp, color = White)
                        Text("${bnDigits(date.dayOfMonth.toString())} অক্টোবর, ${bnDigits(date.year.toString())}", fontSize = 21.sp, color = White)
                    }
                    IconButton(onClick = { }) { Icon(Icons.Default.Share, null, tint = White, modifier = Modifier.size(30.dp)) }
                    IconButton(onClick = { tab = 1 }) { Icon(Icons.Default.Notifications, null, tint = White, modifier = Modifier.size(30.dp)) }
                }
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { progress }, modifier = Modifier.size(250.dp), strokeWidth = 17.dp, color = Orange, trackColor = Color(0xFF315D58))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(next.name, fontSize = 32.sp, color = White, fontWeight = FontWeight.Medium)
                        Text("শুরু হতে বাকি", fontSize = 20.sp, color = White)
                        Text(remaining.split(":").let { "${bnDigits(it[0])}:${bnDigits(it[1])}:০০" }, fontSize = 34.sp, color = White, fontWeight = FontWeight.SemiBold)
                    }
                }
                Row(Modifier.fillMaxWidth().padding(bottom = 18.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Row(Modifier.clip(RoundedCornerShape(28.dp)).background(Color(0xFF17666A)).clickable { picker = true }.padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = White)
                            Text(selected.name, color = White, fontSize = 18.sp, modifier = Modifier.padding(start = 6.dp))
                            Text("⌄", color = White, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                        DropdownMenu(expanded = picker, onDismissRequest = { picker = false }, modifier = Modifier.heightIn(max = 440.dp).background(Panel)) {
                            districts.sortedBy { it.name }.forEach { d ->
                                DropdownMenuItem(text = { Text(d.name, color = White) }, onClick = { selected = d; picker = false })
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.clip(RoundedCornerShape(24.dp)).background(Color(0xFF17666A)).padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Text("☀ সূর্যোদয়: ${fmt(prayers[0].end)}", fontSize = 15.sp, color = White)
                        Text("☼ সূর্যাস্ত: ${fmt(prayers[3].start)}", fontSize = 15.sp, color = White)
                    }
                }
            }
            Column(Modifier.weight(0.51f).fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(Panel)) {
                LazyColumn(Modifier.weight(1f)) {
                    items(prayers) { p ->
                        val active = p.name == next.name
                        Row(
                            Modifier.fillMaxWidth().background(if (active) Color(0xFF24433F) else Color.Transparent).clickable { alarms = if (p.name in alarms) alarms - p.name else alarms + p.name }.padding(horizontal = 28.dp, vertical = 17.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(if (p.name == "ইশা") Icons.Default.NightsStay else Icons.Default.WbSunny, null, tint = if (active) White else Muted, modifier = Modifier.size(38.dp))
                            Text(p.name, modifier = Modifier.weight(1f).padding(start = 22.dp), fontSize = 25.sp, color = if (active) White else Muted)
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${fmt(p.start)} – ${fmt(p.end)}", fontSize = 20.sp, color = if (active) White else Muted)
                                if (p.name in alarms) Text("অ্যালার্ম চালু", fontSize = 12.sp, color = Orange)
                            }
                            Spacer(Modifier.width(18.dp))
                            Box(Modifier.size(30.dp).clip(CircleShape).background(if (p.name in alarms) Orange else Color.Transparent).then(Modifier.padding(1.dp)).clip(CircleShape).background(if (p.name in alarms) Orange else Panel), contentAlignment = Alignment.Center) {
                                Text(if (p.name in alarms) "✓" else "○", color = if (p.name in alarms) White else Muted, fontSize = 22.sp)
                            }
                        }
                        HorizontalDivider(color = Color(0xFF263538), thickness = 1.dp)
                    }
                }
                Row(Modifier.fillMaxWidth().navigationBarsPadding().height(72.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = { tab = 1 }) { Icon(Icons.Default.Notifications, null, tint = White); Spacer(Modifier.width(8.dp)); Text("অ্যালার্ম", color = White, fontSize = 18.sp) }
                    VerticalDivider(Modifier.height(36.dp), color = Muted)
                    TextButton(onClick = { date = date.plusDays(1) }) { Icon(Icons.Default.CalendarMonth, null, tint = White); Spacer(Modifier.width(8.dp)); Text("ক্যালেন্ডার", color = White, fontSize = 18.sp) }
                }
            }
        }
    }
}
