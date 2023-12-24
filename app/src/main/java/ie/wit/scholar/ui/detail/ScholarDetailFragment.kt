package ie.wit.scholar.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ie.wit.scholar.databinding.FragmentScholarDetailBinding
import ie.wit.scholar.ui.auth.LoggedInViewModel
import ie.wit.scholar.ui.report.ReportViewModel
import timber.log.Timber


class ScholarDetailFragment : Fragment() {

    private lateinit var detailViewModel: ScholarDetailViewModel
    private val args by navArgs<ScholarDetailFragmentArgs>()
    private var _fragBinding: FragmentScholarDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val reportViewModel : ReportViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentScholarDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(ScholarDetailViewModel::class.java)
        detailViewModel.observableScholar.observe(viewLifecycleOwner, Observer { render() })

        fragBinding.editScholarButton.setOnClickListener {
            detailViewModel.updateScholar(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.scholarid, fragBinding.scholarvm?.observableScholar!!.value!!)
            findNavController().navigateUp()
        }

        fragBinding.deleteScholarButton.setOnClickListener {
            reportViewModel.delete(loggedInViewModel.liveFirebaseUser.value?.email!!,
                detailViewModel.observableScholar.value?.uid!!)
            findNavController().navigateUp()
        }

        return root
    }

    private fun render() {
        fragBinding.editMessage.setText("A Message")
        fragBinding.editPointsPossible.setText("0")
        fragBinding.scholarvm = detailViewModel
        Timber.i("Retrofit fragBinding.scholarvm == $fragBinding.scholarvm")
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getScholar(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                                    args.scholarid)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}