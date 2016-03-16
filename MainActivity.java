package com.example.oh705.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    int status = 0;  //0 연산자 입력x 1 = + / 2=- / 3=* /4=/
    EditText  number1;
    EditText  number2;
    EditText  result;
    int parseint1;
    int parseint2;
    int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        number1 = (EditText)findViewById(R.id.Edtext1);
        number2 = (EditText)findViewById(R.id.Edtext2);
        result =   (EditText)findViewById(R.id.EdResult);
    }
    public void plus(View v){
        status =1;
        Toast.makeText(getApplicationContext(),"+눌림",Toast.LENGTH_SHORT).show();
    }
    public void minus(View v){
        status =2 ;
    }
    public void mult(View v){
        status =3 ;
    }
    public void div(View v){
        status =4 ;
    }
    public void setResult(){
        parseint1=Integer.parseInt(number1.getText().toString());
        parseint2=Integer.parseInt(number2.getText().toString());
        if(status == 0){
            result.setText("연산자를 선택하세요");
        }
        else if(status ==1){
            temp =parseint1+ parseint2;

        }
        else if(status ==2){
            temp =parseint1- parseint2;
        }
        else if(status ==3){
            temp =parseint1* parseint2;
        }
        else if(status ==4){
            temp =parseint1/ parseint2;
        }
    }
        result.setText(temp);
    public void init(){
        parseint1 = 0;
        parseint2 = 0;
    }

}
