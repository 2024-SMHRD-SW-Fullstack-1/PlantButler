package com.example.plantbutler

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPost : Fragment() {

    // 권한 설정을 위한 상수 선언
    val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    // 카메라, 갤러리 접근에 대한 요청/결과처리를 위한 상수
    val CAMERA_CODE = 98 // 임의의 코드
    val STORAGE_CODE = 99

    lateinit var mContext: Context
    lateinit var mActivity: Activity

    lateinit var ivAddImg: ImageView

    lateinit var encodedImageString: String

    lateinit var queue: RequestQueue

    private var imageUri: Uri? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        queue = Volley.newRequestQueue(mContext)
    
        // 로그인된 아이디 값 가져오기
        val sharedPreferences = activity?.getSharedPreferences("member", Context.MODE_PRIVATE)
        val memId = sharedPreferences?.getString("memId", "default_value").toString()

        ivAddImg= view.findViewById(R.id.ivAddImg)
        val etAddTitle: EditText = view.findViewById(R.id.etAddTitle)
        val etAddContent: EditText = view.findViewById(R.id.etAddContent)
        val tvPostAddAct: TextView = view.findViewById(R.id.postAddAct)
        val tvPostUpdateAct: TextView =view.findViewById(R.id.postUpdateAct)

        val postIdx = arguments?.getString("postIdx")?.toInt()
        val postImg = arguments?.getString("postImg")
        val postTitle = arguments?.getString("postTitle")
        val postContent = arguments?.getString("postContent")

        Log.d("postIdx", postIdx.toString()+ postImg + postTitle + postContent)

        if(postIdx == null) {
            tvPostAddAct.visibility = View.VISIBLE
            tvPostUpdateAct.visibility = View.GONE
        }else {
            tvPostAddAct.visibility = View.GONE
            tvPostUpdateAct.visibility = View.VISIBLE

            Log.d("postImg", postImg.toString())

            if(postImg.toString() != "null") {
                val byteString = Base64.decode(postImg, Base64.DEFAULT)
                val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
                Log.d("byteString", byteString.toString())
                Log.d("byteArray", byteArray.toString())
                ivAddImg.setImageBitmap(byteArray)
            }
            etAddTitle.setText(postTitle)
            etAddContent.setText(postContent)
        }

        ivAddImg.setOnClickListener {
            showBottomSheetDialog()
        }

        tvPostAddAct.setOnClickListener {
            if (etAddTitle.text.toString() == "") {
                Toast.makeText(context, "제목을 작성해주세요 !", Toast.LENGTH_SHORT).show()
            } else if (etAddContent.text.toString() == "") {
                Toast.makeText(context, "본문을 작성해주세요 !", Toast.LENGTH_SHORT).show()
            } else {
                val inputTitle = etAddTitle.text.toString()
                val inputContent = etAddContent.text.toString()
                val encodedImageString: String
                val post: PostVO

                if (imageUri != null) {
                    // 이미지의 비트맵을 가져와서 바이트 배열로 변환
                    Log.d("imageUri", imageUri.toString())
                    val inputStream = mContext.contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val imageByteArray = getByteArrayFromBitmap(bitmap)
                    encodedImageString = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                    post = PostVO(postIdx, memId, encodedImageString, inputContent, null, inputTitle, null)
                } else {
                    post = PostVO(postIdx, memId, null, inputContent, null, inputTitle, null)
                }

                val request = object : StringRequest(
                    Request.Method.POST,
                    "http://192.168.219.60:8089/plantbutler/post/add",
                    { response ->
                        Log.d("addResponse", response.toString())
                        Toast.makeText(context,"게시글 등록 완료",Toast.LENGTH_SHORT).show()
                        val jsonObject = JSONObject(response)
                        val updateIdx = jsonObject.getString("idx")
                        val updateTitle = jsonObject.getString("title")
                        val updateId = jsonObject.getJSONObject("member").getString("id")
                        val updateImg = jsonObject.getString("img")
                        val updateMemImg = jsonObject.getJSONObject("member").getString("img")
                        val updateNick = jsonObject.getJSONObject("member").getString("nick")
                        val updateDate = jsonObject.getString("date")
                        val updateContent = jsonObject.getString("content")
                        val updateViews = 0;

                        Log.d("updateResults", updateIdx+updateTitle+updateId+updateImg+updateMemImg+updateNick)

                        val fragment = PostDetail()
                        val args = Bundle()
                        args.putString("idx", updateIdx)
                        args.putString("title", updateTitle)
                        args.putString("id", updateId)
                        args.putString("postImg", updateImg)
                        args.putString("memImg", updateMemImg)
                        args.putString("nick", updateNick)
                        args.putString("views", updateViews.toString())

                        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val parsedDate = inputDateFormat.parse(updateDate) ?: Date()
                        val formattedDate = outputDateFormat.format(parsedDate)

                        args.putString("date", formattedDate)
                        args.putString("content", updateContent)

                        fragment.arguments = args
                        (context as MainActivity).replaceFragment(fragment)
                    },
                    { error ->
                        Log.d("error", error.toString())
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params: MutableMap<String, String> = HashMap<String, String>()

                        params.put("addPost", Gson().toJson(post))
                        return params
                    }
                }
                queue.add(request)
            }
        }

        tvPostUpdateAct.setOnClickListener {
            if (etAddTitle.text.toString() == "") {
                Toast.makeText(context, "제목을 작성해주세요 !", Toast.LENGTH_SHORT).show()
            } else if (etAddContent.text.toString() == "") {
                Toast.makeText(context, "본문을 작성해주세요 !", Toast.LENGTH_SHORT).show()
            } else {
                val inputTitle = etAddTitle.text.toString()
                val inputContent = etAddContent.text.toString()
                val encodedImageString: String
                val post: PostVO

                if (imageUri != null) {
                    // 이미지의 비트맵을 가져와서 바이트 배열로 변환
                    val inputStream = mContext.contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val imageByteArray = getByteArrayFromBitmap(bitmap)
                    encodedImageString = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                    post = PostVO(postIdx, memId, encodedImageString, inputContent, null, inputTitle, null)
                } else {
                    post = PostVO(postIdx, memId, null, inputContent, null, inputTitle, null)
                }

                Log.d("update__post", post.toString())

                val request = object : StringRequest(
                    Request.Method.POST,
                    "http://192.168.219.60:8089/plantbutler/post/update",
                    { response ->
                        Log.d("updateResponse", response.toString())
                        Toast.makeText(context,"게시글 수정 완료",Toast.LENGTH_SHORT).show()
                        val jsonObject = JSONObject(response)
                        val updateIdx = jsonObject.getString("idx")
                        val updateTitle = jsonObject.getString("title")
                        val updateId = jsonObject.getJSONObject("member").getString("id")
                        val updateImg = jsonObject.getString("img")
                        val updateMemImg = jsonObject.getJSONObject("member").getString("img")
                        val updateNick = jsonObject.getJSONObject("member").getString("nick")
                        val updateDate = jsonObject.getString("date")
                        val updateContent = jsonObject.getString("content")
                        val updateViews = jsonObject.getString("views");

                        Log.d("updateResults", updateIdx+updateTitle+updateId+updateImg+updateMemImg+updateNick)

                        val fragment = PostDetail()
                        val args = Bundle()
                        args.putString("idx", updateIdx)
                        args.putString("title", updateTitle)
                        args.putString("id", updateId)
                        args.putString("postImg", updateImg)
                        args.putString("memImg", updateMemImg)
                        args.putString("nick", updateNick)
                        args.putString("views", updateViews.toString())

                        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val parsedDate = inputDateFormat.parse(updateDate) ?: Date()
                        val formattedDate = outputDateFormat.format(parsedDate)

                        args.putString("date", formattedDate)
                        args.putString("content", updateContent)

                        fragment.arguments = args
                        (context as MainActivity).replaceFragment(fragment)
                    },
                    { error ->
                        Log.d("error", error.toString())
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params: MutableMap<String, String> = HashMap<String, String>()

                        params.put("updatePost", Gson().toJson(post))
                        return params
                    }
                }
                queue.add(request)
            }
        }
        return view
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(mContext)
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
            if(ContextCompat.checkSelfPermission(mContext, permission)
                != PackageManager.PERMISSION_GRANTED) {// 해당 권한이 허용되지 않음
                // 호출이 되면 자동적으로 onRequestPermissionsResult 함수를 호출
                ActivityCompat.requestPermissions(mActivity, permissions, type)
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
        when(requestCode) {
            CAMERA_CODE -> {
                for(grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(mContext, "카메라 권한을 승인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_CODE -> {
                for(grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(mContext, "저장소 권한을 승인해주세요", Toast.LENGTH_SHORT).show()
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
        val byteArray = mContext.contentResolver.openInputStream(uri)?.readBytes()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveImg(fileName: String, mimeType: String, bitmap: Bitmap): Uri? {
        var cv = ContentValues() // => ContentResolver 통해서 콘텐츠를 다룰 수 있음

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        var uri = mContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        if (uri != null) {
            var descriptor = mContext.contentResolver.openFileDescriptor(uri, "w")
            var fos = FileOutputStream(descriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            fos.close()
            cv.clear()

            mContext.contentResolver.update(uri, cv, null, null)
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
                    ivAddImg.setImageURI(imageUri)

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
                    ivAddImg.setImageURI(imageUri)
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