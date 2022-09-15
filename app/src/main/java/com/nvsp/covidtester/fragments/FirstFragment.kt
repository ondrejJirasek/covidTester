package com.nvsp.covidtester.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import com.kotlin.education.android.easytodo.utils.PermissionUtil
import com.nvsp.covidtester.BuildConfig
import com.nvsp.covidtester.R
import com.nvsp.covidtester.ViewModels.FirstFragmentViewModel
import com.nvsp.covidtester.ViewModels.NEGATIVE
import com.nvsp.covidtester.ViewModels.POSITIVE
import com.nvsp.covidtester.activity.MainActivity
import com.nvsp.covidtester.databinding.FragmentFirstBinding
import com.nvsp.covidtester.utils.FileUtil
import com.nvsp.nvmesapplibrary.architecture.BaseFragment
import com.nvsp.nvmesapplibrary.architecture.InfoDialog
import com.nvsp.nvmesapplibrary.constants.Const

import java.io.File
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment<FragmentFirstBinding,FirstFragmentViewModel >(FirstFragmentViewModel::class) {

    private var tempPhotoFile: File? = null
    private val REQUEST_IMAGE_CAPTURE = 100
    private val PERMISSION_CAMERA_REQUEST_CODE = 201


    override val bindingInflater: (LayoutInflater) -> FragmentFirstBinding
        get() = FragmentFirstBinding::inflate

    override fun initViews() {
        binding.positive.setOnClickListener {
            viewModel.setPositive()
        }
        binding.negative.setOnClickListener {
            viewModel.setNegative()
        }
        binding.btnTakePic.setOnClickListener {
            capturePhoto()
        }
        viewModel.login.observe(viewLifecycleOwner,{
            Log.d("USER", "USER_:$it")
        })
        binding.btnSubmit.setOnClickListener {

            viewModel.submit {
                val dialog = InfoDialog(requireContext())
                dialog.showWithMessage("Výsledek Covid testu byl úspěšně nahrán!", Const.LEVEL_CORRECT) {
                    binding.edNote.setText("")
                    lottieState(false)
                    viewModel.reset()

                }
            }
        }
        binding.edNote.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                    viewModel.result.note = p0.toString()
            }
        })

        viewModel.submitEnabler.observe(viewLifecycleOwner,{
            Log.d("FRAGMENT", "state: $it ${viewModel.result}")
            binding.btnSubmit.isEnabled=it
        })

       viewModel.status.observe(viewLifecycleOwner,{
           context?.let {con->
               when(it){
                   POSITIVE->{
                       binding.positive.background = AppCompatResources.getDrawable(con,
                           R.drawable.backgroud_border_red_radius_full
                       )
                       binding.negative.background = AppCompatResources.getDrawable(con,
                           R.drawable.backgroud_border_grey_radius_full
                       )
                   }
                   NEGATIVE->{
                       binding.negative.background = AppCompatResources.getDrawable(con,
                           R.drawable.backgroud_border_green_radius_full
                       )
                       binding.positive.background = AppCompatResources.getDrawable(con,
                           R.drawable.backgroud_border_grey_radius_full
                       )
                   }
                   null->{
                       binding.positive.background = AppCompatResources.getDrawable(con,
                           R.drawable.backgroud_border_grey_radius_full
                       )
                       binding.negative.background = AppCompatResources.getDrawable(con,
                           R.drawable.backgroud_border_grey_radius_full
                       )
                   }
               }
           }

       })
        val today=Calendar.getInstance()
     binding.datePicker1.init(
         today.get(Calendar.YEAR),
         today.get(Calendar.MONTH),
         today.get(Calendar.DAY_OF_MONTH)
     ){view, year, month, day ->
        viewModel.result.date = "$day.${month+1}.$year"
         Log.d("RESULT", viewModel.result.toString())
     }

    }

    override fun onActivityCreated() {

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap= BitmapFactory.decodeFile(tempPhotoFile?.path)
            viewModel.result.photo=bitmap
            viewModel.result.docName = "${viewModel.login.value?.name}"
            lottieState(true)
        }
    }
    private fun capturePhoto(){
        ( activity as MainActivity).let {
            Log.e("CAMERA", "check Permisions")
            if (PermissionUtil.checkCameraStoragePermission(it)) {
                // intent for opening image capture activity
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Checks if there is an app for capturing photos.
                if (takePictureIntent.resolveActivity(it.packageManager) != null) {
                    try {
                        // create an empty file
                        Log.e("CAMERA", "create Empty File")
                        tempPhotoFile = FileUtil.createImageFile(it)
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }

                    // If the file is created, continue.
                    Log.e("CAMERA", "is FIle null? ${tempPhotoFile != null}")
                    if (tempPhotoFile != null) {
                        // the file provider is needed.
                        // Otherwise the photo capturing application will not have
                        // access to the file and will not be able to save photo to it.
                        // Note: the FileProvider is defined in AndroidManifest.xml file
                        // and it has a paths.xml file definition
                        val photoURI = FileProvider.getUriForFile(
                            Objects.requireNonNull(it.applicationContext),
                            BuildConfig.APPLICATION_ID+".provider",
                            tempPhotoFile!!
                        )
                        // set the photo URI to the intent
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        // start the photo capturing activity
                        Log.e("CAMERA", "START ACTIVITX CAM")
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }else{}
                }else
                    Log.e("CAMERA", "not App for photo")
            } else {
                Log.e("CAMERA", "no perrmissions")
                PermissionUtil.requestCameraStoragePermission(it, PERMISSION_CAMERA_REQUEST_CODE)
            }
        }
    }
    fun lottieState(state:Boolean){
        if(state){
            binding.lottie.visibility=View.VISIBLE
            binding.lottie.setAnimation(R.raw.finish)
            binding.lottie.playAnimation()
        }else{
            binding.lottie.cancelAnimation()
            binding.lottie.visibility=View.INVISIBLE
        }

    }

}