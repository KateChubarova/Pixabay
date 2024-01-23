package com.example.pixabay.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DialogDetailsFragment : DialogFragment() {
    private val pixabayViewModel: PixabayViewModel by activityViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.more_details).setMessage(R.string.more_details_message)
            .setPositiveButton(R.string.yes) { dialog, id ->
                pixabayViewModel.positiveButtonClicked()
                dismiss()
            }.setNegativeButton(R.string.no) { dialog, id ->
                dismiss()
            }
        return builder.create()
    }
}