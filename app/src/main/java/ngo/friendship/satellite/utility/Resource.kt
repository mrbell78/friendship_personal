package ngo.friendship.satellite.utility

class Resource<T>(var status: Status, var data: T, var message: String) {

    enum class Status {
        LOADING, SUCCESS, ERROR, UNAUTHORIZED
    }
}