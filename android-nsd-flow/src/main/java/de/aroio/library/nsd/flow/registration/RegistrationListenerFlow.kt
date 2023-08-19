package de.aroio.library.nsd.flow.registration

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import de.aroio.library.nsd.flow.RegistrationFailed
import de.aroio.library.nsd.flow.UnregistrationFailed
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking

internal class RegistrationListenerFlow(
        private val producerScope: ProducerScope<RegistrationEvent>
) : NsdManager.RegistrationListener {
    override fun onRegistrationFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) {
        producerScope.channel.close(RegistrationFailed(nsdServiceInfo, errorCode))
    }

    override fun onUnregistrationFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) {
        producerScope.channel.close(UnregistrationFailed(nsdServiceInfo, errorCode))
    }

    override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
        producerScope.trySendBlocking(RegistrationEvent.ServiceRegistered(nsdServiceInfo))
    }

    override fun onServiceUnregistered(nsdServiceInfo: NsdServiceInfo) {
        producerScope.trySendBlocking(RegistrationEvent.ServiceUnregistered(nsdServiceInfo))
    }

}