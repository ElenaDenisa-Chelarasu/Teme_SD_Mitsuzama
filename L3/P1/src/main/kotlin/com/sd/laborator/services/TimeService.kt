package com.sd.laborator.services
import com.sd.laborator.interfaces.TimeProviderInterface
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class TimeService:TimeProviderInterface {
    override fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(Date())
    }
}