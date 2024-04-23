package com.example.motel_kiosk

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.gridlayout.widget.GridLayout
import com.example.motel_kiosk.databinding.ReserveActivityBinding


class MainActivity : ComponentActivity() {
    private val binding: ReserveActivityBinding by lazy {
        ReserveActivityBinding.inflate(layoutInflater)
    }

    private val dbHelper: SQLiteHelper by lazy {
        SQLiteHelper.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gridLayout = binding.reserveGrid
        val buttonStates = dbHelper.getRoomStates()

        Log.i("Db init", dbHelper.getRoomReservationInfo())

        setGridLayoutListener(gridLayout, buttonStates)
        updateButtonColors(gridLayout, buttonStates)
    }

    private fun setGridLayoutListener(gridLayout: GridLayout,
                                      buttonStates: Array<Array<Boolean>>) {
        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                val view = gridLayout.getChildAt(row * gridLayout.rowCount + col)
                if (view is Button)
                    insertButtonClickListener(view, buttonStates, row, col)
            }
        }
    }

    private fun updateButtonColors(gridLayout: GridLayout,
                                   buttonStates: Array<Array<Boolean>>) {
        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                val view = gridLayout.getChildAt(row * gridLayout.rowCount + col)
                if (view is Button)
                {
                    @RequiresApi(Build.VERSION_CODES.Q)
                    if (buttonStates[row][col]) // 0xDC4242
                        changeButtonBackgroundColor(view, R.color.full_red)
                    else // 0x00BCD4
                        changeButtonBackgroundColor(view, R.color.empty_blue)
                }
            }
        }
    }

    private fun insertButtonClickListener(button: Button,
                                  buttonStates: Array<Array<Boolean>>,
                                  row: Int, col: Int) {
        button.setOnClickListener {
            @RequiresApi(Build.VERSION_CODES.Q)
            if (!buttonStates[row][col]) // 0xDC4242
                changeButtonBackgroundColor(button, R.color.full_red)
            else // 0x00BCD4
                changeButtonBackgroundColor(button, R.color.empty_blue)
            buttonStates[row][col] = !buttonStates[row][col]
            dbHelper.updateData(
                button.text.dropLast(1).toString(),
                button.text.toString(),
                buttonStates[row][col]
            )
            Log.i("Db update", dbHelper.getRoomReservationInfo())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun changeButtonBackgroundColor(button: Button, color: Int)
    {
        button.background.colorFilter =
            BlendModeColorFilter(
                resources.getColor(color, null), BlendMode.MULTIPLY
            )
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}