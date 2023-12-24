package ie.wit.scholar.ui.scholarly

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.scholar.R
import ie.wit.scholar.databinding.FragmentScholarlyBinding
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.ui.auth.LoggedInViewModel
import ie.wit.scholar.ui.report.ReportViewModel

class ScholarlyFragment : Fragment() {

    var totalScholared = 0
    private var _fragBinding: FragmentScholarlyBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var scholarlyViewModel: ScholarlyViewModel
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentScholarlyBinding.inflate(inflater, container, false)
        val root = fragBinding.root
	 setupMenu()
        scholarlyViewModel = ViewModelProvider(this).get(ScholarlyViewModel::class.java)
        scholarlyViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        fragBinding.progressBar.max = 10000
        fragBinding.amountPicker.minValue = 1
        fragBinding.amountPicker.maxValue = 1000

        fragBinding.amountPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to paymentAmount
            fragBinding.paymentAmount.setText("$newVal")
        }
        setButtonListener(fragBinding)

        return root;
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Report
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.scholarError),Toast.LENGTH_LONG).show()
        }
    }

    fun setButtonListener(layout: FragmentScholarlyBinding) {
        layout.scholarlyButton.setOnClickListener {
            val pointsEarned = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalScholared >= layout.progressBar.max)
                Toast.makeText(context,"Scholar points Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val timeline = if(layout.timeline.checkedRadioButtonId == R.id.late) "Late" else "OnTime"
                totalScholared += pointsEarned
                layout.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalScholared)
                layout.progressBar.progress = totalScholared
                scholarlyViewModel.addScholar(loggedInViewModel.liveFirebaseUser,
                    ScholarModel(timeline = timeline, pointsEarned = pointsEarned,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!))
            }
        }
    }

   private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_scholarly, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        totalScholared = reportViewModel.observableScholarsList.value!!.sumOf { it.pointsEarned }
        fragBinding.progressBar.progress = totalScholared
        fragBinding.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalScholared)
    }
}