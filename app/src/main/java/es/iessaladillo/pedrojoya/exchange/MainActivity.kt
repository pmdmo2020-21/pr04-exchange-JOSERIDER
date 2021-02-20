package es.iessaladillo.pedrojoya.exchange

import android.os.Bundle
import android.text.TextWatcher
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import es.iessaladillo.pedrojoya.exchange.databinding.MainActivityBinding
import es.iessaladillo.pedrojoya.exchange.utils.hideSoftKeyboard
import es.iessaladillo.pedrojoya.exchange.utils.isInvalid
import es.iessaladillo.pedrojoya.exchange.utils.isValid
import es.iessaladillo.pedrojoya.exchange.utils.setColorWhenFocused


const val DEFAULT_AMOUNT = 0

class MainActivity : AppCompatActivity() {

    private val binding: MainActivityBinding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }
    private lateinit var amountTextWatcher: TextWatcher

    private var fromCurrencyIndex = 0
    private var toCurrencyIndex = 0

    private lateinit var rdbToCurrencySelected: RadioButton
    private lateinit var rdbFromCurrencySelected: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
    }


    override fun onStart() {
        super.onStart()

        with(binding) {
            mainRdgFromCurrency.setOnCheckedChangeListener { group, _ -> setupRdgFromCurrency(group) }
            mainRdgToCurrency.setOnCheckedChangeListener { group, _ -> setupRdgToCurrency(group) }
            mainEdtAmount.setOnEditorActionListener { _, _, _ -> exchange() }
            mainEdtAmount.setOnFocusChangeListener { _, isFocused -> mainLblAmount.setColorWhenFocused(isFocused, R.color.pink_200) }

            amountTextWatcher = mainEdtAmount.doAfterTextChanged {
                if (it.isInvalid()) {
                    setAmountDefaults()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        with(binding) {
            mainRdgFromCurrency.setOnCheckedChangeListener(null)
            mainRdgToCurrency.setOnCheckedChangeListener(null)
            mainEdtAmount.setOnEditorActionListener(null)
            mainEdtAmount.onFocusChangeListener = null
            mainEdtAmount.removeTextChangedListener(amountTextWatcher)
        }
    }

    private fun setupViews() {
        setListeners()
        setDefaults()
        setupRdgFromCurrency(binding.mainRdgFromCurrency)
        setupRdgToCurrency(binding.mainRdgToCurrency)
    }

    private fun setListeners() {
        binding.btnExchange.setOnClickListener { exchange() }
    }

    private fun setDefaults() {
        setAmountDefaults()

        with(binding) {
            mainRdbFromCurrencyEuro.isChecked = true
            mainRdbToCurrencyDollar.isChecked = true
            mainLblAmount.setColorWhenFocused(true, R.color.pink_200)

            //From currency radiobutton's
            setRadioButtonTag(mainRdbFromCurrencyDollar, Currency.DOLLAR)
            setRadioButtonTag(mainRdbFromCurrencyEuro, Currency.EURO)
            setRadioButtonTag(mainRdbFromCurrencyPound, Currency.POUND)
            //To currency radiobutton's
            setRadioButtonTag(mainRdbToCurrencyDollar, Currency.DOLLAR)
            setRadioButtonTag(mainRdbToCurrencyEuro, Currency.EURO)
            setRadioButtonTag(mainRdbToCurrencyPound, Currency.POUND)
        }
    }


    private fun exchange(): Boolean {

        if (binding.mainEdtAmount.text.isValid()) {

            binding.mainEdtAmount.hideSoftKeyboard()
            val amount: Double = binding.mainEdtAmount.text.toString().toDouble()
            val fromCurrency: Currency = rdbFromCurrencySelected.tag as Currency
            val toCurrency: Currency = rdbToCurrencySelected.tag as Currency

            val dollar = fromCurrency.toDollar(amount)
            val convertedAmount = toCurrency.fromDollar(dollar)

            val result = getString(
                    R.string.exchange_result,
                    "%.2f".format(amount),
                    fromCurrency.symbol,
                    "%.2f".format(convertedAmount),
                    toCurrency.symbol,
            )
            showResult(result)
        } else {
            setAmountDefaults()
        }
        return true
    }


    private fun setupRdgFromCurrency(group: RadioGroup) {
        rdbFromCurrencySelected = ActivityCompat.requireViewById(this, group.checkedRadioButtonId)
        val newSelected = group.indexOfChild(rdbFromCurrencySelected)

        onRadioGroupChangeSelection(
                binding.mainRdgToCurrency,
                fromCurrencyIndex,
                newSelected,
        )

        fromCurrencyIndex = newSelected

        binding.imgFromCurrency.setImageResource((rdbFromCurrencySelected.tag as Currency).drawableResId)
    }

    private fun setupRdgToCurrency(group: RadioGroup) {

        rdbToCurrencySelected = ActivityCompat.requireViewById(this, group.checkedRadioButtonId)
        val newSelected = group.indexOfChild(rdbToCurrencySelected)

        onRadioGroupChangeSelection(
                binding.mainRdgFromCurrency,
                toCurrencyIndex,
                newSelected,
        )

        toCurrencyIndex = newSelected
        binding.imgToCurrency.setImageResource((rdbToCurrencySelected.tag as Currency).drawableResId)
    }

    private fun onRadioGroupChangeSelection(
            radioGroup: RadioGroup,
            oldSelected: Int,
            newSelected: Int,
    ) {
        radioGroup[oldSelected].isEnabled = true
        radioGroup[newSelected].isEnabled = false
    }

    private fun setAmountDefaults() {
        binding.mainEdtAmount.setText(DEFAULT_AMOUNT.toString())
        binding.mainEdtAmount.selectAll()
    }

    private fun showResult(result: String) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
    }

    private fun setRadioButtonTag(editText: RadioButton, tag: Currency) {
        editText.tag = tag
    }


}