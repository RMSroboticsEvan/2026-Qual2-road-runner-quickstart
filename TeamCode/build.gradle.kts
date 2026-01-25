plugins {
    id("dev.frozenmilk.teamcode") version "11.0.0-1.1.0"
    id("dev.frozenmilk.sinister.sloth.load") version "0.2.4"
}

ftc {
    // adds support for kotlin
    kotlin()

    // adds the necessary sdk dependencies
    sdk.TeamCode()
    implementation(dairy.Sloth)
}
