This is Alpha 8 of a major update to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## New features in Alpha 8
- Added ability to set a keybind to quickly enable/disable mod
- Added config option to disable `FRWK EXPLOSIVE` alert
- Added config options to configure how `STALL` and GPWS warnings are relayed to the user:
- - Each warning can now be configured to be relayed via `Screen & Audio`, `Audio Only` or `Screen Only`
- Added config option to customize secondary color
- A graphical representation of vertical speed now appears near the altitude scale

## Changes in Alpha 8
- Automation Modes Display:
- - `THR OVRD` and `PITCH OVRD` are now annunciated
- - Changed the way `THR LK` and `TOGA LK` are annunicated to be easier to read
- - Safety-related inputs are now highlighted with the caution color rather than the secondary color
- - Increased the width the display can take up to reduce text overlap
- Changed the Pitch Limit symbol to be more legible
- Changed the default HUD width to 60%
- Clarified thrust units when selecting Climb and Descend Thrust in VERT mode
- Disabled systems in the Auto Flight Screen now appear in red instead of white

## Fixed issues
- Fixed an issue where alerts would play for a split second even when Alert Sound Volume was set to 0%
- Fixed an issue where alerts would play at the wrong volume for a split second when Alert Sound Volume wasn't equal to 100%
- Fixed an issue where Speed Display and Altitude Display would not be aligned with their scales at some screen resolutions
- Fixed button hitboxes in the System Status Screen
- Fixed scrolling in the System Status Screen
- The `NO SRC AVAIL` alert no longer displays when `THR IDLE` is commanded
