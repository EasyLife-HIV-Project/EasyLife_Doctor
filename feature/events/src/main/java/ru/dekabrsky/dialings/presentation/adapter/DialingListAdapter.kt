package ru.dekabrsky.dialings.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import main.utils.gone
import main.utils.visible
import ru.dekabrsky.dialings.R
import ru.dekabrsky.dialings.databinding.ItemDialingBinding
import ru.dekabrsky.common.domain.model.DialingStatus
import ru.dekabrsky.common.presentation.model.DialingUiModel

class DialingListAdapter(
    private val onItemClick: (DialingUiModel) -> Unit,
    private val onRunClick: (Int) -> Unit
) : RecyclerView.Adapter<DialingListAdapter.DialingHolder>() {

    private var items: MutableList<DialingUiModel> = arrayListOf()

    fun updateItems(newItems: List<DialingUiModel>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialingHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialing, parent, false)

        return DialingHolder(view)
    }

    override fun onBindViewHolder(holder: DialingHolder, position: Int) {
        with(holder.binding) {
            val item = items[position]
            dialingTitle.text = item.name
            cbValue.text = item.callersBaseName
            scenarioValue.text = item.scenarioName
            root.setOnClickListener { onItemClick(item) }
            runDialing.setOnClickListener { onRunClick(item.id) }
            dialingDateTime.text = item.startDate

            when (item.status) {
                DialingStatus.SCHEDULED -> {
                    runLayout.visible()
                    progressLayout.gone()
                }
                DialingStatus.RUN, DialingStatus.DONE -> {
                    runLayout.gone()
                    progressLayout.visible()
                    progressPercent.text = "${item.progress}%"
                    classicProgress.progress = item.progress
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class DialingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemDialingBinding.bind(itemView)
    }
}