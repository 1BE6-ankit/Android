package np.com.ankitkoirala.buttonclick2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int counter = 0;
    private String message = "";

    EditText output;
    EditText userInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        userInput = findViewById(R.id.userInput);
        output = findViewById(R.id.output);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = userInput.getText().toString();
                if(text!=null) {
                    message += (counter++) + ". " + text + "\n";
                    output.setText(message);
                }
            }
        });
    }

}
