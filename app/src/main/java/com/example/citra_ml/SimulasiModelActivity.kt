package com.example.citra_ml

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "autism.tflite"

    private lateinit var resultText: TextView
    private lateinit var age: EditText
    private lateinit var gender: EditText
    private lateinit var ethnicity: EditText
    private lateinit var jundice: EditText
    private lateinit var austim: EditText
    private lateinit var contry_of_res: EditText
    private lateinit var used_app_before: EditText
    private lateinit var result: EditText
    private lateinit var age_desc: EditText
    private lateinit var relation: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        age = findViewById(R.id.age)
        gender = findViewById(R.id.gender)
        ethnicity = findViewById(R.id.ethnicity)
        jundice = findViewById(R.id.jundice)
        austim = findViewById(R.id.austim)
        contry_of_res = findViewById(R.id.contry_of_res)
        used_app_before = findViewById(R.id.used_app_before)
        result = findViewById(R.id.result)
        age_desc = findViewById(R.id.age_desc)
        relation = findViewById(R.id.relation)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                age.text.toString(),
                gender.text.toString(),
                ethnicity.text.toString(),
                jundice.text.toString(),
                austim.text.toString(),
                contry_of_res.text.toString(),
                used_app_before.text.toString(),
                result.text.toString(),
                age_desc.text.toString(),
                relation.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Autism"
                }else if (result == 1){
                    resultText.text = "No Autism"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(11)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String, input10: String): Int{
        val inputVal = FloatArray(10)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        inputVal[9] = input10.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}