package ie.wit.scholar.ui.report

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.scholar.R
import ie.wit.scholar.adapters.ScholarAdapter
import ie.wit.scholar.adapters.ScholarClickListener
import ie.wit.scholar.databinding.FragmentReportBinding
import ie.wit.scholar.main.ScholarApp
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.ui.auth.LoggedInViewModel
import ie.wit.scholar.utils.*
import timber.log.Timber

class ReportFragment : Fragment(), ScholarClickListener {

    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentReportBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = ReportFragmentDirections.actionReportFragmentToScholarlyFragment()
            findNavController().navigate(action)
        }
        showLoader(loader,"Downloading Scholars")
        reportViewModel.observableScholarsList.observe(viewLifecycleOwner, Observer {
                scholars ->
            scholars?.let {
                render(scholars as ArrayList<ScholarModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader,"Deleting Scholar")
                val adapter = fragBinding.recyclerView.adapter as ScholarAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                reportViewModel.delete(reportViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as ScholarModel).uid!!)

                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onScholarClick(viewHolder.itemView.tag as ScholarModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }


    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_report, menu)

                val item = menu.findItem(R.id.toggleScholars) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val toggleScholars: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                toggleScholars.isChecked = false

                toggleScholars.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) reportViewModel.loadAll()
                    else reportViewModel.load()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(scholarsList: ArrayList<ScholarModel>) {
        fragBinding.recyclerView.adapter = ScholarAdapter(scholarsList,this,
            reportViewModel.readOnly.value!!)
        if (scholarsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.scholarsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.scholarsNotFound.visibility = View.GONE
        }
    }

    override fun onScholarClick(scholar: ScholarModel) {
        val action = ReportFragmentDirections.actionReportFragmentToScholarDetailFragment(scholar.uid!!)
        if(!reportViewModel.readOnly.value!!)
            findNavController().navigate(action)
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader,"Downloading Scholars")
            if(reportViewModel.readOnly.value!!)
                reportViewModel.loadAll()
            else
                reportViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader,"Downloading Scholars")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                reportViewModel.liveFirebaseUser.value = firebaseUser
                reportViewModel.load()
            }
        })
        //hideLoader(loader)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}