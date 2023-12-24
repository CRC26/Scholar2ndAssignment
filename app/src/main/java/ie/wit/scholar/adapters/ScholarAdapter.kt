package ie.wit.scholar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.scholar.R
import com.squareup.picasso.Picasso
import ie.wit.scholar.databinding.CardScholarBinding
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.utils.customTransformation
import androidx.core.net.toUri

interface ScholarClickListener {
    fun onScholarClick(scholar: ScholarModel)
}

class ScholarAdapter constructor(private var scholars: ArrayList<ScholarModel>,
                                 private val listener: ScholarClickListener,
                                 private val readOnly: Boolean)
    : RecyclerView.Adapter<ScholarAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardScholarBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val scholar = scholars[holder.adapterPosition]
        holder.bind(scholar,listener)
    }

    fun removeAt(position: Int) {
        scholars.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = scholars.size

    inner class MainHolder(val binding : CardScholarBinding, private val readOnly : Boolean) :
                            RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        fun bind(scholar: ScholarModel, listener: ScholarClickListener) {
            binding.root.tag = scholar
            binding.scholar = scholar
            Picasso.get().load(scholar.profilepic.toUri())
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onScholarClick(scholar) }
            binding.executePendingBindings()
        }
    }
}