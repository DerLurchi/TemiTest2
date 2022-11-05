package com.example.temitest2

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.temitest2.ui.theme.TemiTest2Theme

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.colorspace.Rgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Device
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.robotemi.sdk.Robot
import com.robotemi.sdk.*
import com.robotemi.sdk.Robot.*
import com.robotemi.sdk.Robot.Companion.getInstance
import com.robotemi.sdk.TtsRequest.Companion.create
import com.robotemi.sdk.activitystream.ActivityStreamObject
import com.robotemi.sdk.activitystream.ActivityStreamPublishMessage
import com.robotemi.sdk.constants.*
import com.robotemi.sdk.exception.OnSdkExceptionListener
import com.robotemi.sdk.exception.SdkException
import com.robotemi.sdk.face.ContactModel
import com.robotemi.sdk.face.OnContinuousFaceRecognizedListener
import com.robotemi.sdk.face.OnFaceRecognizedListener
import com.robotemi.sdk.listeners.*
import com.robotemi.sdk.map.Floor
import com.robotemi.sdk.map.MapModel
import com.robotemi.sdk.map.OnLoadFloorStatusChangedListener
import com.robotemi.sdk.map.OnLoadMapStatusChangedListener
import com.robotemi.sdk.model.CallEventModel
import com.robotemi.sdk.model.DetectionData
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
import com.robotemi.sdk.navigation.listener.OnDistanceToDestinationChangedListener
import com.robotemi.sdk.navigation.listener.OnDistanceToLocationChangedListener
import com.robotemi.sdk.navigation.listener.OnReposeStatusChangedListener
import com.robotemi.sdk.navigation.model.Position
import com.robotemi.sdk.navigation.model.SafetyLevel
import com.robotemi.sdk.navigation.model.SpeedLevel
import com.robotemi.sdk.permission.OnRequestPermissionResultListener
import com.robotemi.sdk.permission.Permission
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener
import com.robotemi.sdk.sequence.SequenceModel
import com.robotemi.sdk.telepresence.CallState
import com.robotemi.sdk.voice.ITtsService
import com.robotemi.sdk.voice.model.TtsVoice

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TemiTest2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

// Funktion um den Roboter an einen Ort zu schicken
private fun roboterGeheZu(context: Context, robot:Robot?, ziel:String, beschreibung: String){
    val basistext = "Ich bringe dich "
    val ttsRequest = create(speech = basistext + beschreibung, true)

    Toast.makeText(context, "Ich bringe dich an den Ort: $ziel", Toast.LENGTH_LONG).show()
    if (robot != null) {
        robot.goTo(location = ziel, backwards = false)
        robot.speak(ttsRequest)
    }
}

// Funktion um den Hifletext nochmal anzusagen
private fun hilfeDurchsage(context: Context, robot:Robot?, ansage: String){
    Toast.makeText(context, ansage, Toast.LENGTH_LONG).show()

    if (robot != null) {
        val ttsRequest = create(speech = ansage, true)
        robot.speak(ttsRequest)
    }
}

@Composable
fun Navigation(){
    var robot: Robot?

//  Damit kann die robot variable auch "null" sein und
//  der roboter wird nur instanziert wenn er vorhanden ist
    try {
        robot = getInstance()
    } catch (e: Exception) {
        robot = null
    }

//  Noetig fuer die Toast Nachricht
    val mContext = LocalContext.current
//  Noetig um die App zu schliessen
    val activity = (LocalContext.current as? Activity)

    val ansage : String = "Hi, ich bin Temi. Ich kann dir helfen, " +
            "einen Ort in diesem Gebäude zu finden. " +
            "Wenn ich deinen Ort aufzähle, unterbreche mich mit “Hey Temi” " +
            "und nenne mir deinen Ort. "


//  Start des Screenlayouts
    Box(modifier = Modifier.fillMaxSize()) {

        val hintergrund = painterResource(id = R.drawable.karte_keller)

//      Karte als Hintergrund
        Image(
            painter = hintergrund,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { hilfeDurchsage(mContext, robot, ansage) },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

//      Schliessen Button (versteckt)
        Button(
            onClick = { activity?.finish() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3C3C3C)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(100.dp)
            ) {
            Text(
                text = "",
                fontSize = 30.sp,
            )
        }

//      Hilfebutton
        Button(
            onClick = { hilfeDurchsage(mContext, robot, ansage) },
            modifier = Modifier
                .size(60.dp)
                .offset(30.dp, 30.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF49EACC)),
            contentPadding = PaddingValues(7.dp)
            ) {
            Image(painter = painterResource(id = R.drawable.fragezeichen),
                contentDescription = "Hilfe")
        }

//      Hotspotbuttons
        Goto_Btn(ort = "hinteres labor",
            icon_drawable = R.drawable.hinteres_labor,
            beschreibung = "zum hinteren Labor",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(120.dp, 100.dp)
        )

        Goto_Btn(ort = "aufzug",
            icon_drawable = R.drawable.aufzug,
            beschreibung = "zum Aufzug",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset((-170).dp, (-130).dp)
        )

        Goto_Btn(ort = "treppe",
            icon_drawable = R.drawable.treppe,
            beschreibung = "zur Treppe",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset((-270).dp, (-50).dp)
        )

        Goto_Btn(ort = "eingang",
            icon_drawable = R.drawable.eingang,
            beschreibung = "zum Eingang",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(10.dp, 30.dp)
        )

        Goto_Btn(ort = "herren-wc",
            icon_drawable = R.drawable.h_wc,
            beschreibung = "zum Herren-WC",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset((-30).dp, (-50).dp)
        )

        Goto_Btn(ort = "damen-wc",
            icon_drawable = R.drawable.d_wc,
            beschreibung = "zum Damen-WC",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(50.dp, (-50).dp)
        )

        Goto_Btn(ort = "behinderten-wc",
            icon_drawable = R.drawable.be_wc,
            beschreibung = "zum Behinderten-WC",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(200.dp, (-30).dp)
        )

        Goto_Btn(ort = "sitzecke",
            icon_drawable = R.drawable.sitzecke,
            beschreibung = "zur sitzecke",
            context = mContext,
            robot = robot,
            modifier = Modifier
                .align(Alignment.Center)
                .offset((-20).dp, 120.dp)
            )

//      Legende
        Column (modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 30.dp, bottom = 50.dp),
        ) {
            Text(text = "Legende", color = Color.White, fontWeight = FontWeight(600))
            Legendenpunkt(icon_drawable = R.drawable.be_wc, ziel = "Behinderten-WC")
            Legendenpunkt(icon_drawable = R.drawable.d_wc, ziel = "Damen-WC")
            Legendenpunkt(icon_drawable = R.drawable.h_wc, ziel = "Herren-WC")
            Legendenpunkt(icon_drawable = R.drawable.aufzug, ziel = "Aufzug")
            Legendenpunkt(icon_drawable = R.drawable.treppe, ziel = "Treppe")
            Legendenpunkt(icon_drawable = R.drawable.eingang, ziel = "Eingang")
            Legendenpunkt(icon_drawable = R.drawable.sitzecke, ziel = "Sitzecke")
            Legendenpunkt(icon_drawable = R.drawable.hinteres_labor, ziel = "Hinteres Labor")
        }
    }
}

@Composable
fun Goto_Btn(ort:String,
             icon_drawable:Int,
             beschreibung:String,
             context:Context,
             robot:Robot?,
             modifier : Modifier = Modifier){
    IconButton(
        onClick = { roboterGeheZu(context, robot, ort, beschreibung) },
        modifier = modifier
            .height(100.dp),
        ) {
        Box (contentAlignment = Alignment.Center){
            Image(
                painter = painterResource(id = R.drawable.drop),
                contentDescription = null,
                )
            Image(
                painter = painterResource(id = icon_drawable),
                contentDescription = beschreibung,
                modifier = Modifier
                    .padding(23.dp)
                    .offset(y = (-10).dp)

                )
        }
    }
}

@Composable
fun Legendenpunkt(icon_drawable: Int, ziel : String){
    Row (modifier = Modifier.padding(top = 10.dp)){
        Image(
            painter = painterResource(id = icon_drawable),
            contentDescription = null,
            modifier = Modifier
                .height(20.dp)
                .padding(end = 10.dp),
            colorFilter = ColorFilter.tint(color = Color(0xFF49EACC))
        )
        Text(text = ziel, color = Color.White)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1920px,height=1080px,dpi=224, orientation=landscape"
)
@Composable
fun DefaultPreview() {
    TemiTest2Theme {
        Navigation()
    }
}