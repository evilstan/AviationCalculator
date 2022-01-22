package com.evilstan.aviationcalculator;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalcActivity extends AppCompatActivity {

  private LightButton[] buttonsDigit;
  private LightButton[] buttonsOperate;
  private LightButton btnEnter;
  private LightButton btnReset;

  private ImageView lamp1;

  private MechanicalIndicator[] mechIndTop;
  private MechanicalIndicator[] mechIndBottom;
  private boolean inputLocked = false;
  private boolean inputTop = true;
  private boolean topCleared = true;
  private boolean bottomCleared = true;
  private boolean topNegative = false;

  int[] topNumberArray, bottomNumberArray;
  int firstNumber, secondNumber, result;
  int operand = 4;                                                                                // 0 - plus, 1 - minus, 2 - multiply, 3 - divide

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().hide();
    setContentView(R.layout.activity_calc);
    init();
  }

  public void indicateResult(int result) {
    inputTop = true;
    inputLocked = true;
    for (int i = 0; i < topNumberArray.length; i++) {
      topNumberArray[i] = 0;
      bottomNumberArray[i] = 0;
    }

    setTopIndicator(Math.abs(result));
    setBottomIndicator(0);

    if (result > 999999) {
      setTopIndicator(999999);
      setBottomIndicator(999999);

    } else if (result < -999999) {
      setTopIndicator(999999);
      setBottomIndicator(999999);
      enableLamp(lamp1, true);
      return;
    } else if (result < 0) {
      topNegative = true;
      lamp1.setImageResource(R.drawable.lamp_red_on);
    } else {
      topNegative = false;
      lamp1.setImageResource(R.drawable.lamp_red_off);
    }

    inputLocked = false;
  }

  public void reset() {
    for (int i = 0; i < topNumberArray.length; i++) {
      topNumberArray[i] = 0;
      bottomNumberArray[i] = 0;
    }

    setTopIndicator(0);
    setBottomIndicator(0);

    btnEnter.enable(false);
    btnReset.enable(false);

    topCleared = true;
    bottomCleared = true;

    btnEnter.blink(false);
    btnReset.blink(false);

    inputLocked = false;
    inputTop = true;

    enableLamp(lamp1, false);
    topNegative = false;

    enableOperands(false);
  }

  private void setTopIndicator(int number) {
      if (number > 999999 || number < 0) {
          return;
      }
    enableOperands(true);

    if (topNumberArray[topNumberArray.length - 1] != 0) {
      enableOperands(false);
      btnReset.blink(true);
      return;
    }

    int x = 100000;
    int k = 0;
    topCleared = false;

    if (!inputLocked) {                                                                             // if not reset flag, put digit into array, and shift old elements in it
      for (int i = topNumberArray.length - 1; i > 0; i--) {
        topNumberArray[i] = topNumberArray[i - 1];
      }
      topNumberArray[0] = number;
    } else {
      for (int i = topNumberArray.length - 1; i >= 0;
          i--) {                                // if reset flag, parse input value and fill array
        topNumberArray[i] = number / x;
        number = number % x;
        x = x / 10;
      }
    }

    for (MechanicalIndicator ind : mechIndTop) {                                                //set numbersArry to indicators
      ind.setNumber(topNumberArray[k]);
      k++;
    }
  }

  private void setBottomIndicator(int number) {
      if (number > 999999 || number < 0) {
          return;
      }

    if (bottomNumberArray[bottomNumberArray.length - 1] != 0) {
      btnReset.blink(true);
      btnEnter.enable(false);
      return;
    }

    btnEnter.enable(true);
    btnReset.enable(false);
    bottomCleared = false;

    int x = 100000;
    int k = 0;

    if (!inputLocked) {                                                                             // if not reset flag, put digit into array, and shift old elements in it
      for (int i = bottomNumberArray.length - 1; i > 0; i--) {
        bottomNumberArray[i] = bottomNumberArray[i - 1];
      }
      bottomNumberArray[0] = number;

    } else {
      for (int i = bottomNumberArray.length - 1; i >= 0;
          i--) {                                // if reset flag, parse input value and fill array
        bottomNumberArray[i] = number / x;
        number = number % x;
        x = x / 10;
      }
    }

    for (MechanicalIndicator ind : mechIndBottom) {                                                //set numbersArry to indicators
      ind.setNumber(bottomNumberArray[k]);
      k++;
    }

  }


  private void init() {
    findComponents();
    topNumberArray = new int[6];
    bottomNumberArray = new int[6];
    initListeners();
  }

  private void initListeners() {

    for (LightButton lB : buttonsDigit) {
      lB.setOnTouchListener(digitsListener);
    }

    for (LightButton lB2 : buttonsOperate) {
      lB2.setOnTouchListener(operatorsListener);
    }

    btnEnter.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          String s = "";
          for (int i : topNumberArray) {
            s += i;
          }
          s = new StringBuilder(s).reverse().toString();
          firstNumber = Integer.parseInt(s);

          s = "";
          for (int i : bottomNumberArray) {
            s += i;
          }

          s = new StringBuilder(s).reverse().toString();
          secondNumber = Integer.parseInt(s);

            if (topNegative) {
                firstNumber = -firstNumber;
            }
            if (bottomCleared) {
                secondNumber = -secondNumber;
            }
          switch (operand) {
            case 0:
              result = firstNumber + secondNumber;
              break;
            case 1:
              result = firstNumber - secondNumber;
              break;
            case 2:
              result = firstNumber * secondNumber;
              break;
            case 3:
              try {
                result = firstNumber / secondNumber;
              } catch (ArithmeticException e) {
                result = (-9999991);
                for (LightButton bo : buttonsOperate) {
                  bo.blink(true);
                  btnEnter.blink(true);
                  btnReset.blink(true);
                }
              }
              break;
          }
          indicateResult((int) result);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          btnEnter.enable(false);
          btnReset.enable(true);
          enableOperands(true);
        }
        return false;
      }
    });

    btnReset.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          reset();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          btnReset.enable(false);
        }
        return false;
      }
    });
  }


  View.OnTouchListener operatorsListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        operand = java.util.Arrays.asList(buttonsOperate).indexOf(v);

        if (operand == 1 && topCleared && !inputLocked && inputTop) {
          Toast.makeText(CalcActivity.this, "Nega", Toast.LENGTH_SHORT).show();
          topNegative = true;
          enableLamp(lamp1, true);
        } else {
            if (inputTop) {
                inputTop = false;
            }
          btnReset.enable(false);
        }
      }
      return false;
    }
  };

  View.OnTouchListener digitsListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        if (inputTop) {
          setTopIndicator(java.util.Arrays.asList(buttonsDigit).indexOf(v));
        } else {
          setBottomIndicator(java.util.Arrays.asList(buttonsDigit).indexOf(v));
          enableOperands(false);
        }
      }
      return false;
    }
  };


  private void enableLamp(ImageView lamp, boolean enable) {
    if (enable) {
      lamp.setImageResource(R.drawable.lamp_red_on);
    } else {
      lamp.setImageResource(R.drawable.lamp_red_off);
    }
  }

  private void enableOperands(boolean enable) {
    if (enable) {
      for (LightButton bOp : buttonsOperate) {
        bOp.enable(true);
      }
    } else {
      for (LightButton bOp : buttonsOperate) {
        bOp.enable(false);
        bOp.blink(false);
      }
    }
  }

  private void findComponents() {

    buttonsDigit = new LightButton[10];
    buttonsDigit[0] = findViewById(R.id.btn_zero);
    buttonsDigit[1] = findViewById(R.id.btn_one);
    buttonsDigit[2] = findViewById(R.id.btn_two);
    buttonsDigit[3] = findViewById(R.id.btn_three);
    buttonsDigit[4] = findViewById(R.id.btn_four);
    buttonsDigit[5] = findViewById(R.id.btn_five);
    buttonsDigit[6] = findViewById(R.id.btn_six);
    buttonsDigit[7] = findViewById(R.id.btn_seven);
    buttonsDigit[8] = findViewById(R.id.btn_eight);
    buttonsDigit[9] = findViewById(R.id.btn_nine);

    buttonsOperate = new LightButton[4];
    buttonsOperate[0] = findViewById(R.id.btn_plus);
    buttonsOperate[1] = findViewById(R.id.btn_minus);
    buttonsOperate[2] = findViewById(R.id.btn_multiply);
    buttonsOperate[3] = findViewById(R.id.btn_divide);

    btnEnter = findViewById(R.id.btn_enter);
    btnReset = findViewById(R.id.btn_reset);

    mechIndTop = new MechanicalIndicator[6];
    mechIndTop[0] = findViewById(R.id.m_ind1);
    mechIndTop[1] = findViewById(R.id.m_ind10);
    mechIndTop[2] = findViewById(R.id.m_ind100);
    mechIndTop[3] = findViewById(R.id.m_ind1000);
    mechIndTop[4] = findViewById(R.id.m_ind10000);
    mechIndTop[5] = findViewById(R.id.m_ind100000);

    mechIndBottom = new MechanicalIndicator[6];
    mechIndBottom[0] = findViewById(R.id.m_ind2);
    mechIndBottom[1] = findViewById(R.id.m_ind20);
    mechIndBottom[2] = findViewById(R.id.m_ind200);
    mechIndBottom[3] = findViewById(R.id.m_ind2000);
    mechIndBottom[4] = findViewById(R.id.m_ind20000);
    mechIndBottom[5] = findViewById(R.id.m_ind200000);

    lamp1 = findViewById(R.id.lamp_red1);
  }
}
