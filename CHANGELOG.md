This is Alpha 6 of a major update to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## New features in Alpha 6
- **New Autothrust mode: Vertical Target**: Adjusts thrust based on the target altitude
- **New Autopilot vertical mode: Selected Altitude**: Climbs or descends to reach an altitude, then holds that altitude
- **New Autopilot lateral mode: Selected Coordinates**: Adjusts heading to reach the target coordinates

## Changes in Alpha 6
- **Pressing the A/P Disconnect button when autopilot is already off will now turn off the flight directors**
- Armed, but inactive automation will no longer appear crossed out on the display

## Fixed issues
- Flight directors now move when rolling
- Fixed a THRUST SYS FAULT that could appear when using DaBR thrust
- Fixed an issue that caused Thrust Lock to be reset automatically when using DaBR thrust
- Fixed an issue that caused the autopilot to randomly disconnect when DaBR is enabled
