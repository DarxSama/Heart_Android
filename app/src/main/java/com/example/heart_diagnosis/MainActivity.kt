package com.example.heart_diagnosis

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")

    lateinit var editTextAge: EditText
    lateinit var editTextSex: EditText
    lateinit var editTextBloodPressure: EditText
    lateinit var editTextCholestoral: EditText
    lateinit var editTextBloodSugar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //To run network operations on a main thread or as an synchronous task.
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        editTextAge = findViewById(R.id.editTextAge)
        editTextSex = findViewById(R.id.editTextSex)
        editTextBloodPressure = findViewById(R.id.editTextBloodPressure)
        editTextCholestoral = findViewById(R.id.editTextCholestoral)
        editTextBloodSugar = findViewById(R.id.editTextBloodSugar)

        val buttonPredict = findViewById<Button>(R.id.buttonPredict)

        buttonPredict.setOnClickListener {
            val url = "http://192.168.1.38:3000/api/heart"

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("age", editTextAge.text.toString())
                .add("sex", editTextSex.text.toString())
                .add("blood_pressure", editTextBloodPressure.text.toString())
                .add("cholestoral", editTextCholestoral.text.toString())
                .add("blood_sugar_120", editTextBloodSugar.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val message = data.getDouble("heart").toString()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ระบบวินิจฉัยโรคหัวใจ!!")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", clearText())
                    val alert = builder.create()
                    alert.show()

                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }//button predict
    }//onCreate function

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            editTextAge.text.clear()
            editTextSex.text.clear()
            editTextBloodPressure.text.clear()
            editTextCholestoral.text.clear()
            editTextBloodSugar.text.clear()
            editTextAge.requestFocus()
        }
    }

}//main class