package ru.dekabrsky.italks.navigation

import ru.dekabrsky.avatar.presentation.view.AvatarFlowFragment
import ru.dekabrsky.callersbase.presentation.view.ChatFlowFragment
import ru.dekabrsky.common.presentation.model.ChatsFlowScreenArgs
import ru.dekabrsky.common.presentation.model.EventsFlowScreenArgs
import ru.dekabrsky.dialings.presentation.view.DialingsFlowFragment
import ru.dekabrsky.feature.notifications.common.presentation.model.NotificationsFlowArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.view.NotificationFlowFragment
import ru.dekabrsky.italks.basic.fragments.BasicFlowFragment
import ru.dekabrsky.italks.basic.navigation.FlowFragmentProvider
import ru.dekabrsky.italks.basic.network.model.ReLoginType
import ru.dekabrsky.italks.flows.Flows
import ru.dekabrsky.italks.game.view.fragment.GameFlowFragment
import ru.dekabrsky.italks.profile.view.fragment.ProfileFlowFragment
import ru.dekabrsky.italks.tabs.presentation.fragment.TabsFlowFragment
import ru.dekabrsky.italks.tabs.presentation.model.TabsFlowArgs
import ru.dekabrsky.italks.testerSettings.presentation.view.TesterSettingsFlowFragment
import ru.dekabrsky.login.presentation.view.LoginFlowFragment
import ru.dekabrsky.materials.presentation.view.MaterialsFlowFragment
import ru.dekabrsky.scenarios.presentation.view.PatientsFlowFragment
import ru.dekabrsky.stats.presentation.view.AdultProfileFlowFragment
import javax.inject.Inject

class AppFlowFragmentProvider @Inject constructor() : FlowFragmentProvider {

    override fun provideFlowFragment(screenName: String?, data: Any?): BasicFlowFragment? {
        return when (screenName) {
            Flows.Main.name -> TabsFlowFragment.newInstance(data as TabsFlowArgs)
            Flows.Login.name -> LoginFlowFragment.newInstance(data as? ReLoginType)
            Flows.Chats.name ->
                ChatFlowFragment.newInstance(data as ChatsFlowScreenArgs)
            Flows.Notifications.name ->
                NotificationFlowFragment.newInstance(data as NotificationsFlowArgs)
            Flows.Patients.name -> PatientsFlowFragment.newInstance()
            Flows.Events.name ->
                DialingsFlowFragment.newInstance(data as EventsFlowScreenArgs)
            Flows.Stats.name ->
                AdultProfileFlowFragment.newInstance()
            Flows.TesterSettings.name ->
                TesterSettingsFlowFragment.newInstance()
            Flows.Materials.name ->
                MaterialsFlowFragment.newInstance()
            Flows.Game.name -> GameFlowFragment.newInstance()
            Flows.Avatar.name -> AvatarFlowFragment.newInstance()
            Flows.Profile.name -> ProfileFlowFragment.newInstance()
            else -> null
        }
    }
}