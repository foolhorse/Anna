package me.machao.android.anna.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ContainerActivity : AppCompatActivity(), AFragment.OnAFragmentInteractionListener,
    BFragment.OnBFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, AFragment.newInstance("foo", "bar"), AFragment::class.java.simpleName)
            .commit()
    }

    override fun onAFragmentInteraction() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, BFragment.newInstance("foo", "bar"), BFragment::class.java.simpleName)
            .commit()
    }

    override fun onBFragmentInteraction() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, AFragment.newInstance("foo", "bar"), AFragment::class.java.simpleName)
            .commit()
    }

}
