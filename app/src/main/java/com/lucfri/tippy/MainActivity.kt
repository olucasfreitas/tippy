package com.lucfri.tippy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.animation.ArgbEvaluator
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlin.math.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var etParticipantsNumber: EditText
    private lateinit var btRoundBillUp: Button
    private lateinit var btRoundBillDown: Button
    private lateinit var tvSplitTotal: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etParticipantsNumber = findViewById(R.id.etParticipantsNumber)
        btRoundBillUp = findViewById(R.id.btRoundBillUp)
        btRoundBillDown = findViewById(R.id.btRoundBillDown)
        tvSplitTotal = findViewById(R.id.tvSplitTotal)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT"
        updateTipDescription(INITIAL_TIP_PERCENT)

        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                tvTipPercentLabel.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }
        })

        etParticipantsNumber.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }
        })

        btRoundBillUp.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                roundBillUp()
            }
        })

        btRoundBillDown.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                roundBillDown()
            }
        })
    }

    private fun roundBillDown() {
        if(tvTotalAmount.text.isEmpty()) {
            return
        }

        val totalAmount = floor(tvTotalAmount.text.toString().toDouble())

        val splitValue = divideAmountByParticipants(totalAmount)

        tvTotalAmount.text = "%.2f".format(totalAmount)
        tvSplitTotal.text = "%.2f".format(splitValue)
    }

    private fun roundBillUp() {
        if(tvTotalAmount.text.isEmpty()) {
            return
        }

        val totalAmount = ceil(tvTotalAmount.text.toString().toDouble())

        val splitValue = divideAmountByParticipants(totalAmount)

        tvTotalAmount.text = "%.2f".format(totalAmount)
        tvSplitTotal.text = "%.2f".format(splitValue)
    }

    private fun divideAmountByParticipants(totalAmount: Double): Double {
        val participantsNumber = etParticipantsNumber.text.toString().toDouble()

        return totalAmount / participantsNumber
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int

        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {

        if (etBaseAmount.text.isEmpty() || etParticipantsNumber.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvSplitTotal.text = ""

            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress

        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        val totalDividedByParticipants = divideAmountByParticipants(totalAmount)


        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
        tvSplitTotal.text = "%.2f".format(totalDividedByParticipants)
    }
}