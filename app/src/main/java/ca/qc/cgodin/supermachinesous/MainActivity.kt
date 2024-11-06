package ca.qc.cgodin.supermachinesous
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.qc.cgodin.supermachinesous.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var balance = 100 //  balance
    private var selectedBet = 1 //  bet
    private val secretCode = "WIN100" // Secret code

    private val images = listOf(
        R.drawable.banane,
        R.drawable.charbon,
        R.drawable.diamant,
        R.drawable.emeraude,
        R.drawable.fsa,
        R.drawable.img7,
        R.drawable.piece,
        R.drawable.rubis,
        R.drawable.sacargent,
        R.drawable.tresor
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            balance = it.getInt("balance", 100)
            updateUI()
        }

        setupListeners()
        updateUI()
    }

    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            play()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedBet = when (checkedId) {
                R.id.radio1 -> 1
                R.id.radio2 -> 2
                R.id.radio5 -> 5
                else -> 1
            }
            updateUI()
        }

        binding.editTextCode.setOnEditorActionListener { _, _, _ ->
            checkSecretCode()
            true
        }
    }

    private fun play() {
        if (balance < selectedBet) return

        balance -= selectedBet

        val slot1 = Random.nextInt(images.size)
        val slot2 = Random.nextInt(images.size)
        val slot3 = Random.nextInt(images.size)

        binding.imageView1.setImageResource(images[slot1])
        binding.imageView2.setImageResource(images[slot2])
        binding.imageView3.setImageResource(images[slot3])


        calculateWinnings(slot1, slot2, slot3)

        updateUI()
    }

    private fun calculateWinnings(slot1: Int, slot2: Int, slot3: Int) {
        val isCasseCou = binding.checkBoxCasseCou.isChecked

        if (isCasseCou) {
            when {
                slot1 == 0 && slot2 == 0 && slot3 == 0 -> {
                    val winnings = selectedBet * 100
                    balance += winnings
                    showWinToast(winnings)
                }
                countMatches(slot1, slot2, slot3, 1) >= 2 -> {
                    val winnings = selectedBet * 10
                    balance += winnings
                    showWinToast(winnings)
                }
            }
        } else {
            when {
                slot1 == slot2 && slot2 == slot3 -> {
                    val winnings = selectedBet * 25
                    balance += winnings
                    showWinToast(winnings)
                }
                slot1 == slot2 || slot2 == slot3 || slot1 == slot3 -> {
                    val winnings = selectedBet
                    balance += winnings
                    showWinToast(winnings)
                }
            }
        }
    }

    private fun countMatches(slot1: Int, slot2: Int, slot3: Int, targetImage: Int): Int {
        return listOf(slot1, slot2, slot3).count { it == targetImage }
    }

    private fun checkSecretCode() {
        if (binding.editTextCode.text.toString() == secretCode) {
            balance += 100
            binding.editTextCode.text.clear()
            updateUI()
        }
    }

    private fun showWinToast(amount: Int) {
        Toast.makeText(this, getString(R.string.win_message, amount), Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        binding.textViewBalance.text = getString(R.string.balance_text, balance)

        binding.radio1.isEnabled = balance >= 1
        binding.radio2.isEnabled = balance >= 2
        binding.radio5.isEnabled = balance >= 5
        binding.btnPlay.isEnabled = balance >= selectedBet
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("balance", balance)
    }
}