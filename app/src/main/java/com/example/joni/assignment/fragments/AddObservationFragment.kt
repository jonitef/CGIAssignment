package com.example.joni.assignment.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.joni.assignment.R
import com.example.joni.assignment.db.Observation
import com.example.joni.assignment.db.ObservationDB
import kotlinx.android.synthetic.main.add_observation_fragment.*
import org.jetbrains.anko.doAsync
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.*

class AddObservationFragment: Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_EXTERNAL_STORAGE = 2
    private var currentPhotoPath: String = ""
    private var formattedDate: String = ""
    private var formattedTime: String = ""
    private var orderDate: OffsetDateTime? = null
    private var fileName: String = ""
    private var save: Boolean = false

    // need to setHasOptions to true, so that the menu can be hidden in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_observation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rarity = arrayOf(
                "Common", "Rare", "Extremely rare"
        )
        val spinnerArrayAdapter = ArrayAdapter<String>(context!!, R.layout.spinner_item, rarity)
        raritySpinner.adapter = spinnerArrayAdapter

        imgView.setOnClickListener {
            // if user have already taken/selected image but
            // decides to take another one, delete old
            if (currentPhotoPath.isNotEmpty()){
                File(currentPhotoPath).delete()
            }
            buildAlertDialog()
        }

        saveObservation.setOnClickListener {
            save = true
            if (validateForm(speciesET.text.toString(), notesET.text.toString())){
                createTimeStamp()
                addObservation(speciesET.text.toString(), raritySpinner.selectedItem.toString(), notesET.text.toString(), formattedDate, formattedTime, currentPhotoPath, orderDate!!)
                fragmentManager!!.beginTransaction().replace(R.id.fragment_container, MainFragment()).commit()
            }
        }

        cancelObservation.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragment_container, MainFragment()).commit()
        }

        speciesET.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(speciesET.text)) {
                    speciesTIL.error = "Please enter name of the species"
                }
                else {
                    speciesTIL.error = null
                }
            }

        })

        notesET.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(speciesET.text)) {
                    notesTIL.error = "Please enter notes"
                }
                else {
                    notesTIL.error = null
                }
            }

        })
    }

    // if user press cancels observation creation
    // check if user already took photo, delete it
    override fun onDestroy() {
        super.onDestroy()
        if (!save){
            if (currentPhotoPath.isNotEmpty()){
                File(currentPhotoPath).delete()
            }
        }
    }

    // hide sorting menu in this fragment
    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu!!.findItem(R.id.sorting).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    // result back from capturing image or selecting one
    // display image for the user
    // if photo taken with camera, resize and save it internal
    // if image selected from gallery, save it internal
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            imgView.setImageBitmap(imageBitmap)
            doAsync {
                saveImage(imageBitmap)
            }
        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK){
            val contentUri = data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentUri)
            imgView.setImageBitmap(imageBitmap)
            doAsync {
                saveImage(imageBitmap)
            }
        }
    }

    // callback method for requestPermissions
    // if user grants permission take or select photo
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    takePicture()
                }
                return
            }
            2 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    selectImageFromGallery()
                }
                return
            }
        }
    }

    // check permissions, user need to allow use of camera and/or access
    // to external files to add image to observation
    // if permissions are already granted, user can select or take photo
    private fun checkPermissions(code: Int) {
        if (code == 1){
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.CAMERA), code)
            }
            else{
                takePicture()
            }
        }
        if (code == 2){
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), code)
            }
            else{
                selectImageFromGallery()
            }
        }
    }

    // build and show AlertDialog to the user
    // check permission according to users choice
    private fun buildAlertDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Action")
        builder.setMessage("Do you want to take photo or select one in your phone?")

        builder.setPositiveButton("Take photo"){ _, _ ->
            checkPermissions(1)
        }
        builder.setNegativeButton("Select photo"){ _, _ ->
            checkPermissions(2)
        }
        builder.setNeutralButton("Cancel"){ _, _ ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // open camera, allow user to take photo
    // save it to app internal storage
    private fun takePicture() {
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        fileName = UUID.randomUUID().toString()
        val imgPath = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(fileName, ".jpg", imgPath)

        val imageURI: Uri = FileProvider.getUriForFile(context!!, "com.example.joni.assignment", imageFile)

        currentPhotoPath = imageFile.absolutePath

        if (takePicIntent.resolveActivity(activity!!.packageManager) != null){
            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)

            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    // open gallery, allow user to select existing image
    private fun selectImageFromGallery() {
        val selectImgIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (selectImgIntent.resolveActivity(activity!!.packageManager) != null){
            startActivityForResult(selectImgIntent, REQUEST_EXTERNAL_STORAGE)
        }
    }

    // creates new observation according to users input
    // save it to SQLite database
    private fun addObservation(species: String, rarity: String, notes: String, date: String, time: String, photoPath: String?, orderDate: OffsetDateTime){

        val db = ObservationDB.get(context!!)
        db.observationDao().insert(Observation(0, species, rarity, notes, date, time, photoPath, orderDate))
    }

    // Get current date and time of local timezone(devices timezone)
    private fun createTimeStamp(){
        val calendar= Calendar.getInstance().time

        val formatterDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())

        formattedDate= formatterDate.format(calendar)
        formattedTime = formatterTime.format(calendar)

        orderDate = OffsetDateTime.now()
    }

    // if image taken with camera, it's already saved to internal storage
    // resize it and save it, delete old
    // if selected from gallery, save image in to apps internal storage
    private fun saveImage(bitmap: Bitmap){
        val fileName = UUID.randomUUID().toString()
        val imgPath = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imgPath, "$fileName.jpg")

        val stream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        stream.flush()
        stream.close()

        if (currentPhotoPath.isNotEmpty()){
            File(currentPhotoPath).delete()
        }
        currentPhotoPath = imageFile.absolutePath
    }

    private fun validateForm(species: String, notes: String): Boolean {
        if (species.isEmpty()) {
            speciesTIL.error = "Please enter name of the species"
            return false
        }
        if (notes.isEmpty()) {
            notesTIL.error = "Please enter notes"
            return false
        }
        return true
    }
}