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
import kotlinx.android.synthetic.main.activity_buy_beacons.*

class BuyBeaconsActivity : AppCompatActivity(), BuyBeaconsOptionsAdapter.OptionClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_buy_beacons)

        toolbar.title = getString(string.title_buy_beacons)

        val options = listOf(
            BuyBeaconsOptionsAdapter.BuyOption(R.drawable.ic_aconno,resources.getString(R.string.aconno), "https://aconno.de/products/"),
            BuyBeaconsOptionsAdapter.BuyOption(R.drawable.ic_amazon,resources.getString(R.string.amazon),"https://www.amazon.de/stores/page/0517B6EE-3B24-4488-AF47-DE962A8B8BBA?ingress=2&visitId=6b2d075f-535c-4b15-833a-cb8ea758d693&ref_=ast_bln"),
            BuyBeaconsOptionsAdapter.BuyOption(R.drawable.ic_mouser,resources.getString(R.string.mouser),"https://eu.mouser.com/manufacturer/aconno/"),
            BuyBeaconsOptionsAdapter.BuyOption(R.drawable.ic_tindie,resources.getString(R.string.tindie),"https://www.tindie.com/stores/aconno/items/")
        )

        buy_options.layoutManager = LinearLayoutManager(this)
        buy_options.adapter = BuyBeaconsOptionsAdapter(options, this)
        buy_options.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onBuyOptionClicked(buyOption: BuyBeaconsOptionsAdapter.BuyOption) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = (Uri.parse(buyOption.optionUrl))
        startActivity(intent)
    }


    companion object {

        fun start(context : Context) {
            context.startActivity(Intent(context,BuyBeaconsActivity::class.java))
        }
    }
}