package com.nvsp.covidtester.models

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject

const val COVID_TEST ="CovidTests"

 class Result (value:String

        ){
     var date: String=value
     set(value) {
         field = value
         checker.value=corect()
     }
     var result: Boolean?=null
         set(value) {
             field = value
             checker.value=corect()
         }
     var photo: Bitmap?=null
         set(value) {
             field = value
             checker.value=corect()
         }
     var docName:String=""
     set(value) {
         field = value + date
     }
     var note: String=""


    val checker = MutableLiveData<Boolean>(false)
    private fun corect():Boolean{
        return date!=null && result!=null && photo!=null
    }

     override fun toString(): String {
         return "$date, $result, ${photo!=null}, $note"
     }
    fun toJson():JSONObject{
        val body = JSONObject()
        body.put("Datum", date)
        body.put("Result",result)
        body.put("Note", note)
        body.put("DocumentName", docName)
return body
    }
 fun paramHash():HashMap<String,String>{
     val map = HashMap<String,String>()
     map["Datum"] = date
     map["Result"] = result.toString()
     map["Note"] = note
     map["DocumentName"] = docName
     return map
 }
     fun reset(){
         this.photo=null
         this.docName=""
         this.note=""
         this.result=null


     }
 }