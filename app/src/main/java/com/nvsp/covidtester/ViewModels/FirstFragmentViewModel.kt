package com.nvsp.covidtester.ViewModels


import android.content.Context
import android.graphics.Bitmap
import android.util.Log

import androidx.lifecycle.MutableLiveData
import com.nvsp.covidtester.models.COVID_TEST
import com.nvsp.covidtester.models.Result


import com.nvsp.nvmesapplibrary.architecture.CommunicationViewModel
import com.nvsp.nvmesapplibrary.architecture.InfoDialog
import com.nvsp.nvmesapplibrary.communication.models.Request
import com.nvsp.nvmesapplibrary.communication.volley.ServiceVolley
import com.nvsp.nvmesapplibrary.constants.Requests
import com.nvsp.nvmesapplibrary.database.LibRepository
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

const val POSITIVE="Positive"
const val NEGATIVE="Negative"


class FirstFragmentViewModel( private val api: ServiceVolley, private val localRepository: LibRepository) : CommunicationViewModel(localRepository,api) {

    val status= MutableLiveData<String?>(null)

    val result=Result(getCurrentDate().toString("dd.MM.yyyy"))
    val submitEnabler = result.checker

fun setPositive(){
    result.result= true
    status.value=POSITIVE
}
fun setNegative(){
    result.result=false
    status.value=NEGATIVE
}
    fun submit(finish:(state:Boolean)->Unit){
        val stream = ByteArrayOutputStream()
        result.photo?.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val imageData:ByteArray?=stream.toByteArray()
        imageData?: return
        Log.d("API", "login:${login.value}")
        api.uploadBitmap(
            Request(
                com.android.volley.Request.Method.POST,
                COVID_TEST,
                "",
                login.value,
                null,
                result.paramHash()
            ),
            imageData,
            result.docName,
            "jpg"
        ){status, response ->
            finish(true)
            }
        }

    fun reset(){
        status.value = null
    result.reset()

    }
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }


   private fun getCurrentDate(): Date {
        return Calendar.getInstance().time
    }
}