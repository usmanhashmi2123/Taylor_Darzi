package pk.taylor_darzi.customViews

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import pk.taylor_darzi.R
import pk.taylor_darzi.databinding.NumerickeyboardLayoutBinding
import pk.taylor_darzi.interfaces.NumPadCommandKeyEvent
import pk.taylor_darzi.utils.Utils.hideNativeKeyboard

class NumericKeyboard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    var inputField: TextInputEditText? = null
    private var lineNo = 0
    private val keyValues = SparseArray<String>()
    private var inputConnection: InputConnection? = null
    private var commandKeyEvent: NumPadCommandKeyEvent? = null
    private lateinit var binding:NumerickeyboardLayoutBinding
            private fun init(context: Context, attrs: AttributeSet?) {
                binding = NumerickeyboardLayoutBinding.inflate(LayoutInflater.from(context), this, true)

                binding.button1.setOnClickListener(this)
                binding.button2.setOnClickListener(this)
                binding.button3.setOnClickListener(this)

         binding.button4.setOnClickListener(this)
         binding.button5.setOnClickListener(this)
         binding.button6.setOnClickListener(this)
         binding.button7.setOnClickListener(this)
         binding.button8.setOnClickListener(this)
         binding.button9.setOnClickListener(this)
         binding.button0.setOnClickListener(this)
         binding.buttonDelete.setOnClickListener(this)
         binding.buttonDecimal.setOnClickListener(this)
         binding.buttonClear.setOnClickListener(this)
         binding.buttonDone.setOnClickListener(this)
         binding.buttonLeft.setOnClickListener(this)
         binding.buttonRight.setOnClickListener(this)
                binding.buttonDecimal.text = "."
        keyValues.put(R.id.button_1, "1")
        keyValues.put(R.id.button_2, "2")
        keyValues.put(R.id.button_3, "3")
        keyValues.put(R.id.button_4, "4")
        keyValues.put(R.id.button_5, "5")
        keyValues.put(R.id.button_6, "6")
        keyValues.put(R.id.button_7, "7")
        keyValues.put(R.id.button_8, "8")
        keyValues.put(R.id.button_9, "9")
        keyValues.put(R.id.button_0, "0")
        keyValues.put(R.id.button_decimal, ".")
    }

    fun onDoneClicked() {
        if (commandKeyEvent != null && inputField != null) commandKeyEvent?.onNumPadCommandKeyEvent("done", inputField!!.text.toString(), inputField, lineNo)
    }

    fun changeField() {
        if (commandKeyEvent != null && inputField != null) commandKeyEvent?.onNumPadCommandKeyEvent("changingField", inputField!!.text.toString(), inputField, lineNo)
    }
    fun setFiled(editText: TextInputEditText, lineIndex: Int, numPadCommandKeyEvent: NumPadCommandKeyEvent) {
        inputField = editText
        lineNo = lineIndex
        commandKeyEvent = numPadCommandKeyEvent

        inputField!!.isActivated = true
        inputField!!.requestFocus()
        val dec: Int = inputField!!.text.toString().indexOf(".")
        if (dec > 0) inputField!!.setSelection(dec)
    }
    @JvmName("getInputField1")
    fun getInputField(): TextInputEditText? {
        return inputField
    }

    override fun onClick(view: View) {
        if (inputField == null) return
        hideNativeKeyboard(null)
        val editable = inputField!!.text
        val start = inputField!!.selectionStart
        if (view.id == R.id.button_delete) {
            if (editable != null && start > 0) {
                editable.delete(start - 1, start)
                commandKeyEvent!!.onNumPadCommandKeyEvent("updatedValue", inputField!!.text.toString(), inputField, lineNo)
            }
        } else if (view.id == R.id.button_clear) {
            if (editable != null && editable.length > 0) {
                editable.clear()
                commandKeyEvent!!.onNumPadCommandKeyEvent("updatedValue", "", inputField, lineNo)
            }
        } else if (view.id == R.id.button_right) {
            if (start < inputField!!.length()) inputField!!.setSelection(start + 1)
        } else if (view.id == R.id.button_left) {
            if (start > 0) inputField!!.setSelection(start - 1)
        } else if (view.id == R.id.button_done) {
            onDoneClicked()
        } else {
            if (keyValues[view.id].equals(".", ignoreCase = true) && inputField!!.text.toString().contains(".")) {
                //donothing
            } else {
                /*  val dec = inputField!!.text.toString().indexOf(".")
                var has2decimals = false
               if (dec > 0) has2decimals = inputField!!.length() - dec > 2
               if (has2decimals && start > dec) {
                   //do nothing
               } else*/

                    val value = keyValues[view.id]
                    editable!!.insert(start, value)
                    commandKeyEvent!!.onNumPadCommandKeyEvent("updatedValue", inputField!!.text.toString(), inputField, lineNo)
                
            }
        }
    }

    fun setInputConnection(ic: InputConnection?) {
        inputConnection = ic
    }

    fun destroy() {
        inputConnection = null
    }

    init {
        init(context, attrs)
    }
}