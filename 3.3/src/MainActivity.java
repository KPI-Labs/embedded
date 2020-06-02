package com.example.lab33;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Random;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Comparator;
import static java.util.Map.Entry.*;

public class MainActivity extends AppCompatActivity {
    EditText aInput, bInput, cInput, dInput, yInput;
    int aValue, bValue, cValue, dValue, yValue;
    double mutation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startExecution(View v) {
        aInput = findViewById(R.id.editText1);
        bInput = findViewById(R.id.editText2);
        cInput = findViewById(R.id.editText3);
        dInput = findViewById(R.id.editText4);
        yInput = findViewById(R.id.editText5);
        aValue = Integer.parseInt(aInput.getText().toString());
        bValue = Integer.parseInt(bInput.getText().toString());
        cValue = Integer.parseInt(cInput.getText().toString());
        dValue = Integer.parseInt(dInput.getText().toString());
        yValue = Integer.parseInt(yInput.getText().toString());
        hideKeyboard();

        int[] coeffs = { aValue, bValue, cValue, dValue };
        Equation equation = new Equation(coeffs, yValue);
        Population population = new Population(1000, 4, 10);

        int[] result = population.generate(equation);
        showToast("Answer is: " + Arrays.toString(result));

    }

    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class ShuffleArray {
	public static void shuffle(int[] array) {
		Random rand = new Random();
		for (int i = 0; i < array.length; i++) {
			int j = rand.nextInt(array.length);
			int temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}
}

class Equation {
  public int result;
  public int[] coeffs;
  
  public Equation(int[] coeffs, int result) {
    this.coeffs = coeffs;
    this.result = result;
  }
  
  public int execute(int[] values) {
    int value = 0;
    for (int i = 0; i < this.coeffs.length; i++) {
      value += coeffs[i] * values[i];
    }
    return Math.abs(this.result - value);
  }
}
class Population {
  public int populationSize;
  public int chromosomeSize;
  public int mutationCounter;
  public int[][] chromosomes;
  public int[][] deltas;
  public int[][] tmp;
  static Random random = new Random();

  public Population(
    int populationSize,
    int chromosomeSize,
    int range
  ) {
    this.mutationCounter = 0;
    this.populationSize = populationSize;
    this.chromosomeSize = chromosomeSize;
    this.chromosomes = new int[populationSize][chromosomeSize];
    this.deltas = new int[this.populationSize][2];
    this.tmp = new int[this.chromosomeSize][this.populationSize];

    for (int i = 0; i < populationSize; i++) {
      for (int j = 0; j < chromosomeSize; j++) {
        this.chromosomes[i][j] = random.nextInt(range);
      }
    }
  }

  public int[] next(Equation equation) {
    this.mutationCounter = 0;
    for (int i = 0; i < this.populationSize; i++) {
      int j = i % (this.populationSize - 2);
      int[] entry = deltas[j];
      int index = entry[1]; 
      int delta = entry[0]; 
      int step = (int)(delta * 0.5) + 1;
      int modify = random.nextInt(step) - step / 2;
      this.mutationCounter += 1;
      for (int k = 0; k < this.chromosomeSize; k++) {
        tmp[k][i] = this.chromosomes[index][k] + modify;
      }
    }
  
    for (int i = 0; i < this.chromosomeSize; i++) {
      ShuffleArray.shuffle(tmp[i]);
      for (int j = 0; j < this.populationSize; j++) {
        this.chromosomes[j][i] = tmp[i][j];
      }   
    }
    for (int i = 0; i < this.populationSize; i++) {
      int delta = equation.execute(this.chromosomes[i]);
      this.deltas[i][0] = delta; 
      this.deltas[i][1] = i; 
    }

    Arrays.sort(this.deltas, Comparator.comparingInt(o -> o[0]));

    return this.deltas[0]; 
  }
  
  @RequiresApi(api = Build.VERSION_CODES.N)
  public int[] generate(Equation equation) {
    while(true) {
      int[] result = this.next(equation);
      if (result[0] == 0) {
        return this.mutationCounter;
      }
    }
  }
}
