package com.ssafy.zip.android

import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ssafy.zip.android.databinding.FragmentRecordBoardCreateBinding
import com.ssafy.zip.android.repository.BoardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class RecordBoardCreateFragment : Fragment() {
    private var _binding: FragmentRecordBoardCreateBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private lateinit var activity: MainActivity
    private lateinit var image: Uri

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageUri = it.data!!.data
                image = imageUri!!
                binding.boardImage.setImageURI(image)

                binding.btnPostBoard.setOnClickListener {
                    if (binding.boardContent.text.toString().isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val instance = BoardRepository.getInstance(Application())
                            val body = RequestBody.create(
                                MediaType.get("application/json; charset=utf-8"),
                                binding.boardContent.text.toString().replace("[\r\n]+", "\n")
                            );

                            var response = instance?.postBoard(
                                image = if (image != null) makeMultiPartImage(image) else null,                  // ?????? ????????? binding?????? ????????? ???????????????
                                content = body
                            )
                            if (response.equals("200")) {
                                binding.root.findNavController()
                                    .navigate(R.id.action_recordBoardCreateFragment_pop)
                            } else {
                                Toast.makeText(context, "????????? ????????? ??????????????????", Toast.LENGTH_SHORT)
                            }
                        }
                    }
                }
            }

        }

    fun makeMultiPartImage(uri: Uri): MultipartBody.Part? {

        val contentResolver: ContentResolver = activity.contentResolver
        try {
            val fileName = System.currentTimeMillis().toString()
            val filePath: String = activity.applicationInfo.dataDir + File.separator + fileName
            val file = File(filePath)
            // ??????????????? ?????? uri??? ?????? ???????????? ????????? ???????????? ????????????
            val inputStream = contentResolver.openInputStream(uri)

//            val inputStream = contentResolver.openInputStream(uri)
            // ????????? ???????????? ?????? ??????????????? file ????????? ???????????? ????????? ??????
            val outputStream = FileOutputStream(file) // ????????? ??? ??? ?????? ????????? ??????
            val buf = ByteArray(1024)
            var len: Int
            if (inputStream != null) {
                while (inputStream.read(buf).also { len = it } > 0)
                    outputStream.write(buf, 0, len)
                inputStream.close()
            }
            outputStream.close()
            val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("image", fileName, requestBody)
            return body
        } catch (ignore: IOException) {
//            println("???????????????????????????")
            return null
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBoardCreateBinding.inflate(inflater, container, false)
        val toolbar: Toolbar = binding.boardCreateAppbar
        // ??????????????? appbar title ??????
        toolbar.title = "????????? ??????"

        binding.btnPostBoard.setOnClickListener {
            if (binding.boardContent.text.toString().isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val instance = BoardRepository.getInstance(Application())
                    val body = RequestBody.create(
                        MediaType.get("application/json; charset=utf-8"),
                        binding.boardContent.text.toString().replace("[\r\n]+", "\n")
                    );
                    var response = instance?.postBoard(
                        image = null,               // ?????? ????????? binding?????? ????????? ???????????????
                        content = body
                    )
                    if (response.equals("200")) {
                        binding.root.findNavController()
                            .navigate(R.id.action_recordBoardCreateFragment_pop)
                    } else {
                        Toast.makeText(context, "????????? ????????? ??????????????????", Toast.LENGTH_SHORT)
                    }
                }
            }
        }
        binding.imageButtonContainer.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            photoPickerIntent.type = "image/*"
            startForResult.launch(photoPickerIntent)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}