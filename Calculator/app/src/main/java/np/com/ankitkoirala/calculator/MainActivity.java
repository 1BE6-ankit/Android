package np.com.ankitkoirala.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText newNumber;
    EditText result;
    TextView operation;

    String pendingOp = "";
    boolean toggleMinus = false;

    private final String OPERATION_RETRIEVE_CODE = "GET_OP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newNumber = findViewById(R.id.newNumber);
        result = findViewById(R.id.result);
        operation = findViewById(R.id.operation);

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String btnText = btn.getText().toString();
                newNumber.append(btnText);
            }
        };

        View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String opText = btn.getText().toString();
                String newNum = newNumber.getText().toString();

                if(newNum.length() != 0) {
                    performOperation(opText, newNum);
                }

                newNumber.setText(null);
                pendingOp = opText;
                operation.setText(pendingOp);
            }
        };

        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btnDot = findViewById(R.id.btnDot);

        Button btnEq = findViewById(R.id.btnEq);
        Button btnAdd = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnMul = findViewById(R.id.btnMul);
        Button btnDiv = findViewById(R.id.btnDiv);

        Button negateInput = findViewById(R.id.negateInput);
        negateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMinus = !toggleMinus;
                String newNum = newNumber.getText().toString();
                if(newNum.startsWith("-")) {
                    newNumber.setText(newNum.substring(1));
                } else {
                    newNumber.setText("-" + newNum);
                }
            }
        });

        btn0.setOnClickListener(btnListener);
        btn1.setOnClickListener(btnListener);
        btn2.setOnClickListener(btnListener);
        btn3.setOnClickListener(btnListener);
        btn4.setOnClickListener(btnListener);
        btn5.setOnClickListener(btnListener);
        btn6.setOnClickListener(btnListener);
        btn7.setOnClickListener(btnListener);
        btn8.setOnClickListener(btnListener);
        btn9.setOnClickListener(btnListener);
        btnDot.setOnClickListener(btnListener);


        btnEq.setOnClickListener(opListener);
        btnAdd.setOnClickListener(opListener);
        btnMinus.setOnClickListener(opListener);
        btnMul.setOnClickListener(opListener);
        btnDiv.setOnClickListener(opListener);
    }

    void performOperation(String op, String newNum) {
        if(toggleMinus) {
            toggleMinus = false;
        }

        if(pendingOp.equals("")) {
            result.setText(newNum);
        } else {
            Double operand1 = Double.valueOf(result.getText().toString());
            Double operand2;

            if(newNum.equals(".") || newNum.equals("-.")){
                operand2 = 0.0;
            } else {
                operand2 = Double.valueOf(newNum);
            }
            Double res = operand1;

            if(pendingOp.equals("=")) pendingOp = op;

            switch (pendingOp) {
                case "+":
                    res = operand1 + operand2;
                    break;
                case "-":
                    res = operand1 - operand2;
                    break;
                case "*":
                    res = operand1 * operand2;
                    break;
                case "/":
                    if(operand2 != 0) {
                        res = operand1 / operand2;
                    } else {
                        res = 0.0;
                    }
                    break;
                case "=":
                    res = operand2;
                    break;
            }

            result.setText(String.valueOf(res));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(OPERATION_RETRIEVE_CODE, operation.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        operation.setText((String) savedInstanceState.get(OPERATION_RETRIEVE_CODE));

    }
}
