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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    private lateinit var robot: Robot

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

// Function to generate a Toast
private fun mToast(context: Context, robot:Robot?, ziel:String){
//    var robot: Robot = getInstance()
    Toast.makeText(context, "Ich bringe dich an den Ort: $ziel", Toast.LENGTH_LONG).show()
    if (robot != null) {
        robot.goTo(location = ziel, backwards = true,)
    }
}

@Composable
fun Navigation(){
    var robot: Robot?

    try {
        robot = getInstance()
    } catch (e: Exception) {
        robot = null
    }


    Box(modifier = Modifier.fillMaxSize()) {

        val mContext = LocalContext.current
        val activity = (LocalContext.current as? Activity)

        val hintergrund = painterResource(id = R.drawable.karte_keller)

        Image(
            painter = hintergrund,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            activity?.finish()
        },
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 60.dp)
            ) {
            Text(
                text = "Exit",
                fontSize = 30.sp,
            )
        }

        IconButton(
            onClick = { mToast(mContext, robot, "sitzecke") },
            modifier = Modifier
                .height(100.dp)
                .align(Alignment.Center)
            ,) {
            Image(
                painter = painterResource(id = R.drawable.temi),
                contentDescription = null )
        }
        IconButton(
            onClick = { mToast(mContext, robot, "home base") },
            modifier = Modifier
                .height(100.dp)
                .align(Alignment.CenterStart)
                .padding(start = 60.dp)) {
            Image(
                painter = painterResource(id = R.drawable.temi),
                contentDescription = null )
        }
        IconButton(
            modifier = Modifier
                .height(100.dp)
                .align(Alignment.CenterEnd)
                .padding(end= 60.dp)
            ,
            onClick = { mToast(mContext, robot, "hinteres labor") }) {
            Image(
                painter = painterResource(id = R.drawable.temi),
                contentDescription = null )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1920dp,height=1080dp,dpi=224"
)
@Composable
fun DefaultPreview() {
    TemiTest2Theme {
        Navigation()
    }
}