package com.example.plantbutler

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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class EditActivity : AppCompatActivity() {

    lateinit var binding:ActivityEditBinding
    var CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    var CAMERA_CODE:Int = 98
    lateinit var encodedImageString:String
    lateinit var queue: RequestQueue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        //기본 프로필 사진 설정
        binding.ivEdit.setImageResource(R.drawable.profile)

        // 프로필 사진 변경
        binding.btnEditImg.setOnClickListener {
            callCamera()
            callReq()
        }
        // 닉네임 변경
        binding.btnEditNick.setOnClickListener {
            val pm = Member(memId.toString(),memPw.toString(),memNick,null)
            val request = StringRequest(
                Request.Method.POST,
                "http://192.168.219.60:8089/plantbutler/mypage/nick",
                {response->

                },
                {error->

                }
            )
        }
        // 비밀번호 변경
        binding.btnEditPw.setOnClickListener {

        }
        // 회원 탈퇴
        binding.btnDeleteAct.setOnClickListener {

        }

    }
    fun checkPermission(permissions:Array<String>, type:Int) : Boolean {
        for (permission in permissions){
            // 권한 허용 확인
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissions, type)
                return false
            }
        }
        return true
    }
    fun callCamera() {
        // 카메라, 저장(스토리지) 사용 권한 허용 확인
        if(checkPermission(CAMERA, CAMERA_CODE)) {
            // 카메라 사용
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 사진 촬영
            startActivityForResult(intent,CAMERA_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val img = data?.extras?.get("data") as Bitmap
        // 갤러리 저장
        val uri = saveImg(randomFileName(), "image/jpeg", img)

        // 프로필 사진 띄우기
        binding.ivEdit.setImageURI(uri)
        // 사진을 서버로 보내기
        if(uri != null){
            encodedImageString = getBase64FromUri(uri)
        }
    }
    // 랜덤 이미지 파일 이름 생성
    fun randomFileName() :String {
        // 년도,월,시간대
        return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
    }
    fun getBase64FromUri(uri: Uri) : String{
        // image -> byte Array -> String 변환
        val byteArray = this.contentResolver.openInputStream(uri)?.readBytes()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveImg(fileName: String, mimeType: String, bitmap: Bitmap) : Uri?{

        var cv = ContentValues()

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // MediaStore 이미지 파일을 저장
        var uri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        if(uri != null) {
            var descripter = this.contentResolver.openFileDescriptor(uri, "w")
            var fos = FileOutputStream(descripter?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            fos.close()
            cv.clear()

            this.contentResolver.update(uri, cv, null, null)
        }
        return uri
    }
    fun callReq() {
        queue = Volley.newRequestQueue(this)

        val request = object: StringRequest(
            Request.Method.POST,
            "http://192.168.219.60:8089/plantbutler/mypage/image",
            {response->
                Log.d("response",response.toString())
            },
            {error->
                Log.d("error",error.toString())
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val params:MutableMap<String,String> = HashMap<String,String>()
                params.put("image", encodedImageString)
                return params
            }
        }
        queue.add(request)
    }




}