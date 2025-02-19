package ru.dekabrsky.easylife.flows

import ru.dekabrsky.easylife.basic.navigation.Flow

class Flows {

    object Main : Flow("FLOW_MAIN")

    object Login : Flow("FLOW_LOGIN") {
        const val SCREEN_LOGIN = "SCREEN_LOGIN"
        const val SCREEN_PIN_LOGIN = "SCREEN_PIN_LOGIN"
    }

    object Chats : Flow("FLOW_CALLERS_BASE") {
        const val SCREEN_CHATS_LIST = "SCREEN_BASES_LIST"
        const val SCREEN_CHAT_CONVERSATION = "SCREEN_BASES_DETAILS"
        const val SCREEN_CHAT_NEW_CONTACTS = "SCREEN_CHAT_NEW_CONTACTS"
    }

    object Notifications: Flow("FLOW_NOTIFICATIONS") {
        const val SCREEN_CHILD_NOTIFICATIONS_LIST = "SCREEN_CHILD_NOTIFICATIONS_LIST"
        const val SCREEN_DOCTOR_NOTIFICATIONS_LIST = "SCREEN_DOCTOR_NOTIFICATIONS_LIST"
        const val SCREEN_EDIT_NOTIFICATION = "SCREEN_EDIT_NOTIFICATION"
        const val SCREEN_REST_OF_PILLS_LIST = "SCREEN_REST_OF_PILLS_LIST"
        const val SCREEN_EDIT_REST_OF_PILLS = "SCREEN_EDIT_REST_OF_PILLS"
    }

    object Materials : Flow("FLOW_MATERIALS") {
        const val SCREEN_MATERIALS_LIST = "SCREEN_MATERIALS_LIST"
        const val SCREEN_MATERIAL_DETAILS = "SCREEN_MATERIAL_DETAILS"
    }

    object Events : Flow("FLOW_DIALING") {
        const val SCREEN_DIALINGS_LIST = "SCREEN_DIALINGS_LIST"
        const val SCREEN_DIALING_DETAILS = "SCREEN_DIALING_DETAILS"
    }

    object Patients : Flow("FLOW_SCENARIOS") {
        const val SCREEN_PATIENTS_LIST = "SCREEN_SCENARIOS_LIST"
        const val SCREEN_PATIENT_DETAILS = "SCREEN_SCENARIO_DETAILS"
        const val SCREEN_PATIENTS_CODES = "SCREEN_PATIENTS_CODES"
        const val SCREEN_INVITE_PATIENT = "SCREEN_INVITE_PATIENT"
        const val SCREEN_SELECT_PARENT = "SCREEN_SELECT_PARENT"
    }

    object Stats : Flow("FLOW_STATS") {
        const val SCREEN_MAIN_STATS = "SCREEN_MAIN_STATS"
    }

    object TesterSettings : Flow("FLOW_TESTER_SETTINGS") {
        const val SCREEN_TESTER_SETTINGS = "SCREEN_TESTER_SETTINGS"
    }

    object Game : Flow("FLOW_GAME") {
        const val SCREEN_START_GAME = "SCREEN_GABE"
        const val SCREEN_MAIN_ROOM = "SCREEN_MAIN_ROOM"
        const val SCREEN_GARDEN = "SCREEN_GARDEN"
        const val SCREEN_FLAPPY = "SCREEN_FLAPPY"
        const val SCREEN_FOOTBALL = "SCREEN_FOOTBALL"
        const val SCREEN_LEAVES = "SCREEN_LEAVES"
        const val SCREEN_FIFTEEN = "SCREEN_FIFTEEN"
        const val SCREEN_AVATAR_CUSTOMIZATION = "SCREEN_AVATAR_CUSTOMIZATION"
    }

    object Profile : Flow("FLOW_PROFILE") {
        const val SCREEN_PROFILE = "SCREEN_PROFILE"
    }

    object Calendar : Flow("FLOW_CALENDAR") {
        const val SCREEN_CALENDAR = "SCREEN_CALENDAR"
    }

    object Avatar : Flow("FLOW_AVATAR") {
        const val SCREEN_AVATAR_SELECTION = "SCREEN_AVATAR_SELECTION"
    }

    object Common : Flow("COMMON") {
        const val SCREEN_BOTTOM_INFO = "SCREEN_BOTTOM_INFO"
        const val SCREEN_WEB_VIEW = "SCREEN_WEB_VIEW"
    }
}