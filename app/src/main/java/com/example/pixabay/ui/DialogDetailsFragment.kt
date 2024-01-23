package com.example.pixabay.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pixabay.Image

class DialogDetailsFragment : DialogFragment() {

    companion object {
        const val IMAGE = "image"

        fun newInstance(image: Image): DialogDetailsFragment {
            val args = Bundle().apply {
                putSerializable(IMAGE, image)
            }
            return DialogDetailsFragment().apply {
                arguments = args
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("More")
            .setMessage("Do you want to see more details?")
            .setPositiveButton("Yes") { dialog, id ->
                dismiss()
            }
            .setNegativeButton("No") { dialog, id ->
                dismiss()
            }
        // Create the AlertDialog object and return it
        return builder.create()
    }
}