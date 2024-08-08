package com.example.plantbutler

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.plantbutler.databinding.ActivityEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class EditActivity : AppCompatActivity() {

    lateinit var binding:ActivityEditBinding
    var imageUri: Uri? = null
    var CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    var CAMERA_CODE:Int = 98
    var STORAGE_CODE:Int = 99
    lateinit var encodedImageString:String
    lateinit var queue: RequestQueue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queue  = Volley.newRequestQueue(this)

        // 로그인된 정보 가져오기
        val sharedPreferences = getSharedPreferences("member", Context.MODE_PRIVATE)
        val memNick = sharedPreferences?.getString("memNick","planty")
        val memId = sharedPreferences?.getString("memId", "id")
        val memPw = sharedPreferences?.getString("memPw", "pw")
        val memImg = sharedPreferences?.getString("memImg", "Img")

        //val ivEdit = findViewById<ImageView>(R.id.)
        //val btnCam
        //val etEditNick
        //val btnEditNick
        //val etEditPw
        //val etEditPw2
        //val btnEditPw
        //valDeleteAct

        // 프로필 사진 변경
        binding.ivEdit.setOnClickListener {
            showBottomSheetDialog()
        }
        // 닉네임 변경
        binding.etEditNick.hint = memNick
        binding.btnEditNick.setOnClickListener {
            val inputNick = binding.etEditNick.text
            val pm = Member(memId.toString(),memPw.toString(),inputNick.toString(),null)

            val request = object :StringRequest(
                Request.Method.POST,
                "http://192.168.219.61:8089/plantbutler/mypage/nick",
                {response->
                    Log.d("nickChange",response.toString())
                    Toast.makeText(this, "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show()
                },
                {error->
                    Log.d("nickError",error.toString())
                }
            ){
                override fun getParams():MutableMap<String,String>{
                    val params:MutableMap<String,String> = HashMap<String,String>()
                    params.put("Member", Gson().toJson(pm))

                    return params
                }
            }
            queue.add(request)
        }
        // 비밀번호 변경
        binding.btnEditPw.setOnClickListener {
            val inputPw = binding.etEditPw.text.toString()
            val inputPw2 = binding.etEditPw2.text.toString()
            val pm = Member(memId.toString(),inputPw,null,null)
            if(inputPw != inputPw2){
                Toast.makeText(this, "입력한 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }else{
                val request = object:StringRequest(
                    Request.Method.POST,
                    "http://192.168.219.61:8089/plantbutler/mypage/pw",
                    {response->
                        Log.d("pwChange",response.toString())
                        Toast.makeText(this, "변경되었습니다", Toast.LENGTH_SHORT).show()
                    },
                    {error->
                        Log.d("pwError",error.toString())
                    }
                ){
                    override fun getParams():MutableMap<String,String>{
                        val params:MutableMap<String,String> = HashMap<String,String>()
                        params.put("Member", Gson().toJson(pm))

                        return params
                    }
                }
                queue.add(request)
            }

        }
        // 회원 탈퇴
        binding.btnDeleteAct.setOnClickListener {
            val inputPw = binding.etEditPw.text.toString()
            val inputPw2 = binding.etEditPw2.text.toString()
            val pm = Member(memId.toString(),inputPw,null,null)
            if(inputPw == "" || inputPw2 == ""){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if (inputPw != inputPw2){
                Toast.makeText(this, "입력한 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }else{
                val request = object :StringRequest(
                    Request.Method.POST,
                    "http://192.168.219.61:8089/plantbutler/mypage/delete",
                    {response->
                        Toast.makeText(this, "탈퇴되었습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                    },
                    {error->

                    }
                ){
                    override fun getParams():MutableMap<String,String>{
                        val params:MutableMap<String,String> = HashMap<String,String>()
                        params.put("Member", Gson().toJson(pm))

                        return params
                    }
                }
                queue.add(request)
            }
        }

    }
    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        val btnCancel: Button = view.findViewById(R.id.btn_cancel)
        val btnTakePhoto: Button = view.findViewById(R.id.btn_take_photo)
        val btnChoosePhoto: Button = view.findViewById(R.id.btn_choose_photo)

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        btnTakePhoto.setOnClickListener {
            callCamera()
            bottomSheetDialog.dismiss()
        }

        btnChoosePhoto.setOnClickListener {
            callGallery()
            bottomSheetDialog.dismiss()
        }
    }

    fun checkPermission(permissions: Array<String>, type: Int): Boolean {
        for(permission in permissions) {
            // 각 권한이 허용되어있는지 확인
            if(ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {// 해당 권한이 허용되지 않음
                // 호출이 되면 자동적으로 onRequestPermissionsResult 함수를 호출
                ActivityCompat.requestPermissions(this, permissions, type)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_CODE -> {
                for(grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_CODE -> {
                for(grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun randomFileName(): String {
        return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
    }

    fun getBase64FromUri(uri: Uri): String {
        // image(uri) to Base64 String
        // image -> byte Array -> String
        val byteArray = contentResolver.openInputStream(uri)?.readBytes()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveImg(fileName: String, mimeType: String, bitmap: Bitmap): Uri? {
        var cv = ContentValues() // => ContentResolver 통해서 콘텐츠를 다룰 수 있음

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        var uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        if (uri != null) {
            var descriptor = contentResolver.openFileDescriptor(uri, "w")
            var fos = FileOutputStream(descriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            fos.close()
            cv.clear()

            contentResolver.update(uri, cv, null, null)
        }
        return uri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> { // 카메라
                    val img = data?.extras?.get("data") as Bitmap

                    // 갤러리 저장 (이미지 파일 이름 - 랜덤으로 생성, 이미지 파일의 형식 (mimeType), 저장할 이미지)
                    imageUri = saveImg(randomFileName(), "image/jpeg", img)

                    // ImageView에 띄우기
                    binding.ivEdit.setImageURI(imageUri)

                    if (imageUri != null) {
                        encodedImageString = getBase64FromUri(imageUri!!)
                    }
                }
            }
        }
    }

    fun callCamera() {
        // 카메라, 저장(스토리지) 사용 권한 허용 확인 => 카메라 사용
        if(checkPermission(CAMERA, CAMERA_CODE)) {
            // 카메라 사용
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 사진 촬영
            // onActivityResult 함수 자동 호출
            startActivityForResult(intent, CAMERA_CODE)
        }
    }

    // 갤러리 open
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                callGallery()
            }
        }
    // 가져온 사진 보여주기
    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    imageUri = it
                    binding.ivEdit.setImageURI(imageUri)
                }
            }
        }

    fun callGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    // 비트맵 > 바이트 배열
    fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

}