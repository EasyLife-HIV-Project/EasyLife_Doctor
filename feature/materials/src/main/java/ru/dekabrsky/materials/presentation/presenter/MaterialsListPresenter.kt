package ru.dekabrsky.materials.presentation.presenter

import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.materials.presentation.mapper.MaterialsUiMapper
import ru.dekabrsky.materials.presentation.model.MaterialDetailsUiModel
import ru.dekabrsky.materials.presentation.view.MaterialsListView
import javax.inject.Inject

class MaterialsListPresenter@Inject constructor(
    private val router: FlowRouter,
    private val uiMapper: MaterialsUiMapper
) : BasicPresenter<MaterialsListView>(router) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        load()
    }

    private fun load() {
        viewState.setMaterialsList(uiMapper.mapMaterials())
    }

    fun onMaterialClick(model: MaterialDetailsUiModel) {
        router.navigateTo(Flows.Materials.SCREEN_MATERIAL_DETAILS, model)
    }
}