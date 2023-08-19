package com.example.networkdiscovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkdiscovery.adapter.DiscoveryAdapter
import com.example.networkdiscovery.adapter.toDiscoveryRecord
import com.google.android.material.button.MaterialButton
import de.aroio.library.nsd.flow.NsdManagerFlow
import de.aroio.library.nsd.flow.discovery.DiscoveryConfiguration
import de.aroio.library.nsd.flow.discovery.DiscoveryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var toggleButton: MaterialButton
    private lateinit var statusTextView: TextView
    private lateinit var recyclerView: RecyclerView

    private val nsdManagerFlow: NsdManagerFlow by lazy { NsdManagerFlow(applicationContext) }
    private val adapter: DiscoveryAdapter = DiscoveryAdapter()
    private var job: Job = Job().apply { cancel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById(R.id.toggle)
        statusTextView = findViewById(R.id.status)
        recyclerView = findViewById(R.id.recycler_view)


        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = adapter

        toggleButton.setOnClickListener {
            toggle()
        }
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDiscovery()
    }


    private fun updateUI(){
        when {
            job.isActive -> R.string.activity_main_status_discovery_on
            else -> R.string.activity_main_status_discovery_off
        }.also { statusTextView.setText(it) }
    }

    private fun toggle(){
        if (job.isActive) stopDiscovery() else startDiscovery()
    }


    private fun startDiscovery(){

        Log.e(TAG,"startDiscovery")
        job = CoroutineScope(Dispatchers.Main).launch {
            nsdManagerFlow.discoverServices(DiscoveryConfiguration("_services._dns-sd._udp"))
                .flowOn(Dispatchers.IO)
                .onCompletion { Log.e(TAG,"Completion caused by $it") }
                .collect{event->
                    when(event){
                        is DiscoveryEvent.DiscoveryStarted->Log.d(TAG, "DiscoveryStarted event: $event")
                        is DiscoveryEvent.DiscoveryStopped->Log.d(TAG,"DiscoveryStopped event: $event")
                        is DiscoveryEvent.DiscoveryServiceFound -> adapter.addItem(event.service.toDiscoveryRecord())
                        is DiscoveryEvent.DiscoveryServiceLost -> adapter.removeItem(event.service.toDiscoveryRecord())

                    }

                }
        }
        updateUI()

    }

        private fun stopDiscovery() {
            Log.e(TAG,"stopDiscovery")
            job.cancel()
            adapter.clear()
            updateUI()
        }

}