This is Alpha 9 of a major update to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## New features in Alpha 9
- Reworked the heading scale

## Changes in Alpha 9
- Added ECAM action on how to silence the `AUTO FLT AP OFF` alarm
- Disabling auto thrust or autopilot in the Auto Flight screen no longer triggers alarms
- Moving the camera on ground no longer disconnects the autopilot
- Pitch limit mode is no longer displayed when on ground

## Fixed issues
- Elytra will no longer auto-open when climbing ladders
- Fixed visual distortion when the HUD is being resized
- Fixed `HUD SPD DSPL FAULT` and `HUD ALT DSPL FAULT` appearing when the HUD is being resized
- Fixed `HUD HDG DSPL FAULT` appearing in extremely rare circumstances
- Fixed various usability issues in the System Status screen
- Thrust mode is now highlighted when thrust cannot be used (no source or reverse unsupported)
- `THR NO SRC AVAIL` now works correctly when using auto thrust in SPEED mode
