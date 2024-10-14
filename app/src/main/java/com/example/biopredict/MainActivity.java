package com.example.biopredict;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    // Variables de entrada
    EditText inputField1;
    EditText inputField2;
    EditText inputField3;
    EditText inputField4;
    EditText inputField5;

    Button predictBtn;
    TextView resultTV;

    Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Asociar los campos de entrada con el layout
        inputField1 = findViewById(R.id.editTextNumber1);
        inputField2 = findViewById(R.id.editTextNumber2);
        inputField3 = findViewById(R.id.editTextNumber3);
        inputField4 = findViewById(R.id.editTextNumber4);
        inputField5 = findViewById(R.id.editTextNumber5);

        predictBtn = findViewById(R.id.button);
        resultTV = findViewById(R.id.textView);

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener las entradas como texto y luego convertirlas a float
                float value1 = Float.parseFloat(inputField1.getText().toString());
                float value2 = Float.parseFloat(inputField2.getText().toString());
                float value3 = Float.parseFloat(inputField3.getText().toString());
                float value4 = Float.parseFloat(inputField4.getText().toString());
                float value5 = Float.parseFloat(inputField5.getText().toString());

                // Cambiar el tamaño del array para 5 entradas
                float[][] inputs = new float[1][5];
                inputs[0][0] = value1;
                inputs[0][1] = value2;
                inputs[0][2] = value3;
                inputs[0][3] = value4;
                inputs[0][4] = value5;

                // Realizar la inferencia y mostrar el resultado
                float result = doInference(inputs);
                resultTV.setText("Result: " + result);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public float doInference(float[][] input) {
        float[][] output = new float[1][1];
        interpreter.run(input, output);
        return output[0][0];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("linear.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }
}
