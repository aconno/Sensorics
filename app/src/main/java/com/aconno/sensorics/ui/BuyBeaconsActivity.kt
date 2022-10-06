package com.aconno.sensorics.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aconno.sensorics.R
import com.aconno.sensorics.R.*
import com.aconno.sensorics.adapter.BuyBeaconsOptionsAdapter
import com.aconno.sensorics.databinding.ActivityBuyBeaconsBinding
import com.aconno.sensorics.model.BuyOption

class BuyBeaconsActivity : AppCompatActivity(), BuyBeaconsOptionsAdapter.OptionClickListener {

    private lateinit var binding: ActivityBuyBeaconsBinding

    private val buyOptions by lazy {
        listOf(
            BuyOption(
                R.drawable.ic_aconno,
                resources.getString(R.string.aconno),
                "https://aconno.de/products/"
            ),
            BuyOption(
                R.drawable.ic_amazon,
                resources.getString(R.string.amazon),
                "https://www.amazon.de/stores/page/0517B6EE-3B24-4488-AF47-DE962A8B8BBA?ingress=2&visitId=6b2d075f-535c-4b15-833a-cb8ea758d693&ref_=ast_bln"
            ),
            BuyOption(
                R.drawable.ic_mouser,
                resources.getString(R.string.mouser),
                "https://eu.mouser.com/manufacturer/aconno/"
            ),
            BuyOption(
                drawable.ic_tindie,
                resources.getString(string.tindie),
                "https://www.tindie.com/stores/aconno/items/"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBuyBeaconsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolbar.title = getString(string.title_buy_beacons)

        binding.buyOptions.layoutManager = LinearLayoutManager(this)
        binding.buyOptions.adapter = BuyBeaconsOptionsAdapter(buyOptions, this)
        binding.buyOptions.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }

    override fun onBuyOptionClicked(buyOption: BuyOption) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = (Uri.parse(buyOption.optionUrl))
        startActivity(intent)
    }


    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, BuyBeaconsActivity::class.java))
        }
    }
}