rootProject.name = "Should I Stay"

include(":app")
include(":libraries:archi")
include(":libraries:pratik")
include(":libraries:network")

buildCache {
    local {
        isEnabled = true
    }
}
