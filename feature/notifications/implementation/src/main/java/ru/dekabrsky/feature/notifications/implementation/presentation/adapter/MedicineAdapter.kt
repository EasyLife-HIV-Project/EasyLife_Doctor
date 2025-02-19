package ru.dekabrsky.feature.notifications.implementation.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import main.utils.onTextChange
import main.utils.setBoolVisibility
import ru.dekabrsky.feature.notifications.common.domain.model.DosageUnit
import ru.dekabrsky.feature.notifications.implementation.R
import ru.dekabrsky.feature.notifications.implementation.databinding.ItemMedicineBinding
import ru.dekabrsky.feature.notifications.implementation.presentation.model.MedicineItemUiModel

class MedicineAdapter(
    private val dataStore: MedicineDataStore
): RecyclerView.Adapter<MedicineAdapter.MedicineHolder>() {

    init {
        setHasStableIds(true)
    }

    interface MedicineDataStore {
        var items: MutableList<MedicineItemUiModel>
        fun onNameChanged(text: String, position: Int)
        fun onDosageChanged(text: String, position: Int)
        fun onNoteChanged(text: String, position: Int)
        fun onUnitChanged(unit: DosageUnit, position: Int)
        fun onDelete(position: Int)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)

        return MedicineHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineHolder, position: Int) {
        val item = dataStore.items[position]

        with (holder.binding) {
            tabletName.setText(item.name)
            tabletDosage.setText(item.dosage)
            dosageUnit.check(if (item.unit == DosageUnit.ML) R.id.dosageMl else R.id.dosagePill)
            tabletNote.setText(item.note)

            tabletName.onTextChange { dataStore.onNameChanged(it, position) }
            tabletDosage.onTextChange { dataStore.onDosageChanged(it, position) }
            tabletNote.onTextChange { dataStore.onNoteChanged(it, position) }
            dosageUnit.setOnCheckedChangeListener { _, id ->
                dataStore.onUnitChanged(if (id == R.id.dosageMl) DosageUnit.ML else DosageUnit.PILL, position)
            }

            deleteItem.setOnClickListener { dataStore.onDelete(position) }
            deleteItem.setBoolVisibility(dataStore.items.size > 1)
        }
    }

    override fun getItemCount(): Int = dataStore.items.size

    class MedicineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemMedicineBinding.bind(itemView)
    }
}