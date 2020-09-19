package com.example.mohamed.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mohamed.trackmysleepquality.R
import com.example.mohamed.trackmysleepquality.database.SleepDatabase
import com.example.mohamed.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar


class SleepTrackerFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        val sleepTrackerViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.setLifecycleOwner(this)


        sleepTrackerViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.cleared_message),
                        Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()

                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })


        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer { night ->
            night?.let {
                              this.findNavController().navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))

                sleepTrackerViewModel.doneNavigating()
            }
        })

        sleepTrackerViewModel.navigateToSleepDataQuality.observe(this, Observer { night ->
            night?.let {

                this.findNavController().navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepDetailFragment(night))
                sleepTrackerViewModel.onSleepDataQualityNavigated()
            }
        })

        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager

        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =  when (position) {
                0 -> 3
                else -> 1
            }
        }

        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
            sleepTrackerViewModel.onSleepNightClicked(nightId)
        })

        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        return binding.root
    }
}
