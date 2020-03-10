package com.example.squadmaker.view.detailedfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.squadmaker.R
import com.example.squadmaker.view.widgets.detailedfragment.DetailedCharacterInformationView
import com.example.squadmaker.viewmodel.DetailedViewModelImpl
import kotlinx.android.synthetic.main.fragment_detailed.*
import kotlinx.android.synthetic.main.view_detailed_character_information.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailedFragment : Fragment(),
    DetailedCharacterInformationView.CharacterViewInteraction {

    // region fields

    private val detailedViewModel: DetailedViewModelImpl by viewModel()

    // endregion

    // region Lifecycle overrides functions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detailed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateProgressBarVisibility(true)
        updateCharacterInformationView()
        initObservers()
        attachListeners()
    }

    override fun onStop() {
        super.onStop()
        detailedViewModel.removeDetailedCharacter()
    }

    // endregion

    // region Private Functions

    private fun updateCharacterInformationView() {
        val id = arguments?.let { DetailedFragmentArgs.fromBundle(it).characterId }
        if (id != null) {
            detailedViewModel.updateDetailedCharacter(id)
        }
    }

    private fun initObservers() {
        detailedViewModel.getDetailedCharacter().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                detailed_character_information_view.updateCharacterInformation(it)
            }
        })
        detailedViewModel.getComics().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                val extraAvailableComics = it[0].availableComics.minus(it.size)
                val shouldShowMoreLabel = extraAvailableComics > 0
                updateComicViewVisibility(true, shouldShowMoreLabel)
                detailed_character_comic_view.updateComics(it)
                detailed_Character_available_comics_view.updateAvailability(extraAvailableComics)
            } else {
                updateComicViewVisibility(shouldShowComics = false, shouldShowMoreLabel = false)
            }
        })
    }

    private fun updateComicViewVisibility(shouldShowComics: Boolean, shouldShowMoreLabel: Boolean) {
        if (shouldShowComics) {
            detailed_character_comic_view.visibility = VISIBLE
        } else {
            detailed_character_comic_view.visibility = GONE
        }
        if (shouldShowMoreLabel) {
            detailed_Character_available_comics_view.visibility = VISIBLE
        } else {
            detailed_Character_available_comics_view.visibility = GONE
        }
    }

    private fun attachListeners() {
        detailed_character_information_view.setListener(this)
    }

    @SuppressLint("ResourceType")
    private fun showToast(text: String) {
        // fake margin
        val textToShow = "  ".plus(text).plus("  ")
        val toast = Toast.makeText(context, textToShow, Toast.LENGTH_LONG)
        toast.view.setBackgroundColor(resources.getInteger(R.color.color_second_layer))
        toast.show()
    }

    private fun showConfirmationControlDialog(isSquadMember: Boolean, name: String) {
        val builder = context?.let { AlertDialog.Builder(it) }

        builder?.setTitle("Attention!")
        builder?.setMessage("Are you sure that you want to remove $name from your squad List?")

        builder?.setPositiveButton("Yes") { _, _ ->
            detailed_character_information_view.switchIcons()
            val toastText = name.plus(" ").plus(" removed from your squad.")
            showToast(toastText)
            detailedViewModel.updateSquadList(isSquadMember)
        }

        builder?.setNegativeButton("No") { _, _ ->
        }
        val dialog: AlertDialog? = builder?.create()

        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(resources.getColor(R.color.marvelRedDark, null))
        dialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(resources.getColor(R.color.marvelRedDark, null))

        dialog?.show()
    }

    private fun updateProgressBarVisibility(shouldBeShown: Boolean) {
        if (shouldBeShown) {
            character_detailed_view_pb.visibility = VISIBLE
        } else {
            character_detailed_view_pb.visibility = GONE
        }
    }


    // endregion

    // region CharacterViewInteraction functions

    override fun fabClicked(isSquadMember: Boolean, name: String) {
        if (isSquadMember) {
            showConfirmationControlDialog(isSquadMember, name)
        } else {
            val text =
                name
                    .plus(" ")
                    .plus("added to your squad!")
            detailed_character_information_view.switchIcons()
            detailedViewModel.updateSquadList(isSquadMember)
            showToast(text)
        }
    }

    override fun signalViewReady() {
        updateProgressBarVisibility(false)
    }

    // endregion

}