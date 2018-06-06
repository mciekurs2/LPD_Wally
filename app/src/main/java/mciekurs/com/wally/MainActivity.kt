package mciekurs.com.wally

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import mciekurs.com.wally.fragments.FragmentAdapter
import mciekurs.com.wally.fragments.ImageFragment
import mciekurs.com.wally.fragments.UserFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewPager()
        setupItemSelect()
        setupPageListener()


    }

    private fun setupViewPager(){
        val adapter = FragmentAdapter(supportFragmentManager)
        viewPager_mainActivity.adapter = adapter
        adapter.addFragment(UserFragment())
        adapter.addFragment(ImageFragment())
        adapter.addFragment(UserFragment())
        adapter.notifyDataSetChanged()
    }

    private fun setupItemSelect(){
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.item_home -> {
                    viewPager_mainActivity.currentItem = 0
                }
                R.id.item_images -> {
                    viewPager_mainActivity.currentItem = 1
                }
                R.id.item_profile -> {
                    viewPager_mainActivity.currentItem = 2
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    private fun setupPageListener(){
        viewPager_mainActivity.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                bottom_navigation.menu.getItem(position).isChecked = true
            }


        })
    }

}
