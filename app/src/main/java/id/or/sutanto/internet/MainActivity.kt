package id.or.sutanto.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.or.sutanto.internet.ui.theme.InternetTheme

private const val TAG = "InternetBorder"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InternetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .internetConnectionBorder()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.internetConnectionBorder(): Modifier {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(true) }
    
    // Callback for network changes
    val networkCallback = remember {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "onAvailable")
                isOnline = true
            }
            
            override fun onLost(network: Network) {
                Log.d(TAG, "onLost")
                isOnline = false
            }
        }
    }
    
    // Register and unregister the network callback
    DisposableEffect(context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        onDispose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }
    
    return if (isOnline) {
        Log.d(TAG, "set green")
        this.then(Modifier.border(width = 16.dp, color = Color.Green))
    } else {
        Log.d(TAG, "set red")
        this.then(Modifier.border(width = 16.dp, color = Color.Red))
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InternetTheme { Greeting("Android") }
}
