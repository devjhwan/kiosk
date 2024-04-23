package com.example.motel_kiosk

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.motel_kiosk.databinding.ReserveActivityBinding


class MainActivity : ComponentActivity() {
    private lateinit var binding: ReserveActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReserveActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayout = binding.reserveGrid
        val buttonStates = Array(gridLayout.rowCount) { Array(gridLayout.columnCount) { false } }
        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                val view = gridLayout.getChildAt(row * gridLayout.rowCount + col)
                if (view is Button) {
                    view.setOnClickListener {
                        @RequiresApi(Build.VERSION_CODES.Q)
                        if (!buttonStates[row][col]) // 0xDC4242
                            view.background.colorFilter =
                                BlendModeColorFilter(
                                    resources.getColor(R.color.full_red, null),
                                    BlendMode.MULTIPLY
                                )
                        else // 0x00BCD4
                            view.background.colorFilter =
                                BlendModeColorFilter(
                                    resources.getColor(R.color.empty_blue, null),
                                    BlendMode.MULTIPLY
                                )
                        buttonStates[row][col] = !buttonStates[row][col]
                    }
                }
            }
        }
    }
}