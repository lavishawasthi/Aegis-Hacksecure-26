package com.obsidian.aegis.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.obsidian.aegis.R
import com.obsidian.aegis.databinding.ContentCustomizationBinding
import com.obsidian.aegis.databinding.FragmentCustomizationBinding
import com.obsidian.aegis.helpers.setViewTint
import com.obsidian.aegis.helpers.updateOpacity
import com.obsidian.aegis.helpers.updateSize
import com.obsidian.aegis.models.IndicatorOpacity
import com.obsidian.aegis.models.IndicatorPosition
import com.obsidian.aegis.models.IndicatorSize


class CustomizationFragment : Fragment(R.layout.fragment_customization) {

    lateinit var binding: FragmentCustomizationBinding

    lateinit var viewModel: HomeViewModel

    lateinit var customizationBinding: ContentCustomizationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCustomizationBinding.bind(view)

        customizationBinding = binding.contentCustomization
        viewModel = (activity as HomeActivity).viewModel

        setUpView()
        setUpObservers()
        setUpListeners()
    }

    private fun setUpView() {
        customizationBinding.multiSwitchHorizontalHeight.selectedTab = viewModel.indicatorPosition.value!!.horizontal
        customizationBinding.multiSwitchVerticalHeight.selectedTab = viewModel.indicatorPosition.value!!.vertical
        customizationBinding.multiSwitchSize.selectedTab = viewModel.indicatorSize.value!!.ordinal
        customizationBinding.multiSwitchOpacity.selectedTab = viewModel.indicatorOpacity.value!!.ordinal
    }

    private fun setUpObservers() {
        viewModel.indicatorForegroundColor.observe(viewLifecycleOwner, {
            customizationBinding.tileForeGround.setViewTint(it)
            binding.indicatorsLayout.ivCam.setViewTint(it)
            binding.indicatorsLayout.ivMic.setViewTint(it)
            binding.indicatorsLayout.ivLoc.setViewTint(it)
        })

        viewModel.indicatorBackgroundColor.observe(viewLifecycleOwner, {
            customizationBinding.tileBackGround.setViewTint(it)
            binding.indicatorsLayout.llBackground.setBackgroundColor(Color.parseColor(it))
        })

        viewModel.indicatorSize.observe(viewLifecycleOwner, {
            binding.indicatorsLayout.ivCam.updateSize(it.size)
            binding.indicatorsLayout.ivMic.updateSize(it.size)
            binding.indicatorsLayout.ivLoc.updateSize(it.size)
        })

        viewModel.indicatorOpacity.observe(viewLifecycleOwner, {
            binding.indicatorsLayout.root.updateOpacity(it.opacity)
        })
    }

    private fun setUpListeners() {
        customizationBinding.tileForeGround.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                    .setTitle("Indicator Foreground Color")
                    .setColorShape(ColorShape.SQAURE)
                    .setColorSwatch(ColorSwatch._200)
                    .setDefaultColor(viewModel.indicatorForegroundColor.value!!)
                    .setColorListener { _, colorHex ->
                        viewModel.setIndicatorForegroundColor(colorHex)
                    }.show()
        }

        customizationBinding.tileBackGround.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                    .setTitle("Indicator Background Color")
                    .setColorShape(ColorShape.SQAURE)
                    .setColorSwatch(ColorSwatch._900)
                    .setDefaultColor(viewModel.indicatorBackgroundColor.value!!)
                    .setColorListener { _, colorHex ->
                        viewModel.setIndicatorBackgroundColor(colorHex)
                    }.show()
        }

        customizationBinding.multiSwitchVerticalHeight.setOnSwitchListener { vertical, _ ->
            val horizontal = customizationBinding.multiSwitchHorizontalHeight.selectedTab
            viewModel.setIndicatorPosition(IndicatorPosition.getIndicatorPosition(vertical, horizontal))
        }

        customizationBinding.multiSwitchHorizontalHeight.setOnSwitchListener { horizontal, _ ->
            val vertical = customizationBinding.multiSwitchVerticalHeight.selectedTab
            viewModel.setIndicatorPosition(IndicatorPosition.getIndicatorPosition(vertical, horizontal))
        }

        customizationBinding.multiSwitchSize.setOnSwitchListener { size, _ ->
            viewModel.setIndicatorSize(IndicatorSize.values()[size])
        }

        customizationBinding.multiSwitchOpacity.setOnSwitchListener { opacity, _ ->
            viewModel.setIndicatorOpacity(IndicatorOpacity.values()[opacity])
        }

    }
}