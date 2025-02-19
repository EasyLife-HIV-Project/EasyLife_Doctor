package ru.dekabrsky.feature.notifications.implementation.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.dekabrsky.feature.notifications.implementation.R
import ru.dekabrsky.feature.notifications.implementation.databinding.ItemMedicineBinding
import ru.dekabrsky.feature.notifications.implementation.databinding.ItemRestOfPillBinding
import ru.dekabrsky.feature.notifications.implementation.presentation.model.RestOfPillUiModel

class RestOfPillAdapter(
    private val dataStore: RestOfPillDataStore
): RecyclerView.Adapter<RestOfPillAdapter.RestOfPillHolder>() {
    interface RestOfPillDataStore {
        val items: List<RestOfPillUiModel>
        fun onItemDeleteClicked(itemId: String)
        fun onItemEditClicked(itemId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestOfPillHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rest_of_pill, parent, false)

        return RestOfPillHolder(view)
    }

    override fun getItemCount() = dataStore.items.size

    override fun onBindViewHolder(holder: RestOfPillHolder, pos: Int) {
        val item = dataStore.items[pos]

        holder.binding.pillName.text = item.name

        holder.binding.inDayCount.key.text = "Количество в сутки"
        holder.binding.inDayCount.value.text = item.inDayCount.toString()

        holder.binding.restCount.key.text = "Текущий остаток"
        holder.binding.restCount.value.text = item.restCount.toString()

        holder.binding.date.key.text = "Ожидаемая дата окончания"
        holder.binding.date.value.text = item.date

        holder.binding.deleteIcon.setOnClickListener { dataStore.onItemDeleteClicked(item.id) }
        holder.binding.editIcon.setOnClickListener { dataStore.onItemEditClicked(item.id) }

        holder.binding.restOfPillsAreGone.isVisible = item.areGone
        holder.binding.root.setBackgroundColor(
            holder.binding.root.context.getColor(
                if (item.areGone) R.color.red_50 else R.color.white
            )
        )
    }

    class RestOfPillHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRestOfPillBinding.bind(itemView)
    }
}