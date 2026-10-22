package com.parvej.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parvej.simplecalculator.ui.theme.SimpleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var storedValue by remember { mutableStateOf(0.0) }
    var lastOperation by remember { mutableStateOf<Char?>(null) }
    var resetOnNextInput by remember { mutableStateOf(false) }

    fun formatNumber(value: Double): String {
        val rounded = if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString().removeSuffix(".0").removeSuffix(".00")
        }
        return if (rounded == "-0") "0" else rounded
    }

    fun appendDigit(digit: String) {
        if (resetOnNextInput) {
            display = digit
            resetOnNextInput = false
            return
        }

        if (display == "0" && digit != ".") {
            display = digit
            return
        }

        if (digit == "." && display.contains(".")) return

        display = display + digit
    }

    fun applyOperator(op: Char) {
        val current = display.toDoubleOrNull() ?: 0.0
        if (lastOperation != null && !resetOnNextInput) {
            val result = when (lastOperation) {
                '+' -> storedValue + current
                '-' -> storedValue - current
                '×' -> storedValue * current
                '÷' -> if (current == 0.0) 0.0 else storedValue / current
                else -> current
            }
            display = formatNumber(result)
            storedValue = result
        } else {
            storedValue = current
        }

        lastOperation = op
        resetOnNextInput = true
    }

    fun evaluate() {
        if (lastOperation == null) return

        val current = display.toDoubleOrNull() ?: 0.0
        val result = when (lastOperation) {
            '+' -> storedValue + current
            '-' -> storedValue - current
            '×' -> storedValue * current
            '÷' -> if (current == 0.0) 0.0 else storedValue / current
            else -> current
        }

        display = formatNumber(result)
        storedValue = result
        lastOperation = null
        resetOnNextInput = true
    }

    fun clearAll() {
        display = "0"
        storedValue = 0.0
        lastOperation = null
        resetOnNextInput = false
    }

    fun backspace() {
        if (display.length <= 1) {
            display = "0"
            return
        }
        display = display.dropLast(1)
    }

    fun toggleSign() {
        val currentValue = display.toDoubleOrNull() ?: 0.0
        display = formatNumber(-currentValue)
    }

    fun percent() {
        val currentValue = display.toDoubleOrNull() ?: 0.0
        display = formatNumber(currentValue / 100.0)
    }

    val buttons = listOf(
        listOf("C", "%", "±", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "⌫", "="),
    )

    val context = androidx.compose.ui.platform.LocalContext.current
    val websiteUrl = "https://bparvej.api.bd"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = display,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { label ->
                            CalculatorButton(
                                label = label,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when (label) {
                                        "C" -> clearAll()
                                        "%" -> percent()
                                        "±" -> toggleSign()
                                        "÷" -> applyOperator('÷')
                                        "×" -> applyOperator('×')
                                        "−" -> applyOperator('-')
                                        "+" -> applyOperator('+')
                                        "=" -> evaluate()
                                        "⌫" -> backspace()
                                        "." -> appendDigit(".")
                                        else -> {
                                            if (label.toIntOrNull() != null || label == "0") {
                                                appendDigit(label)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Developed by @bparvej",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                        context.startActivity(intent)
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when (label) {
        "+", "−", "×", "÷", "=", "C" -> MaterialTheme.colorScheme.primaryContainer
        "%", "±", "⌫" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when (label) {
        "+", "−", "×", "÷", "=", "C" -> MaterialTheme.colorScheme.onPrimaryContainer
        "%", "±", "⌫" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val buttonShape = RoundedCornerShape(18.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .background(Color.Transparent)
            .padding(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = buttonShape
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}
